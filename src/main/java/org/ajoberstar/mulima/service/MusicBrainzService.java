package org.ajoberstar.mulima.service;

import io.micrometer.core.instrument.util.IOUtils;
import org.ajoberstar.mulima.meta.CuePoint;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.util.XmlDocuments;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ajoberstar.mulima.util.XmlDocuments.getText;

public final class MusicBrainzService {
  private static final Logger logger = LogManager.getLogger(MusicBrainzService.class);

  private final HttpClient http;
  private final String metaflacPath;
  private final ProcessService process;

  private final ReentrantLock rateLimitLock;

  public MusicBrainzService(HttpClient http, String metaflacPath, ProcessService process) {
    this.http = http;
    this.metaflacPath = metaflacPath;
    this.process = process;
    this.rateLimitLock = new ReentrantLock();
  }

  public String calculateDiscId(List<Metadata> tracks, Path flacFile) {
    Function<Metadata, Integer> trackNum = track -> track.getTags().getOrDefault("tracknumber", List.of()).stream()
        .findFirst()
        .map(Integer::parseInt)
        .orElse(-1);

    var firstTrack = tracks.stream()
        .map(trackNum)
        .min(Comparator.naturalOrder())
        .orElse(-1);
    var lastTrack = tracks.stream()
        .map(trackNum)
        .max(Comparator.naturalOrder())
        .orElse(-1);

    var sampleRate = Long.parseLong(process.execute(metaflacPath, "--show-sample-rate", flacFile.toString())
        .assertSuccess()
        .getOutput()
        .trim());
    var sampleTotal = Long.parseLong(process.execute(metaflacPath, "--show-total-samples", flacFile.toString())
        .assertSuccess()
        .getOutput()
        .trim());

    var offsets = tracks.stream()
        .collect(Collectors.toMap(trackNum, this::calculateOffset));
    var leadOutOffset = sampleTotal * 75 / sampleRate + 150;
    offsets.put(0, (int) leadOutOffset);

    var str = new StringBuilder();
    str.append(String.format("%02X", firstTrack));
    str.append(String.format("%02X", lastTrack));
    for (var i = 0; i < 100; i++) {
      var offset = offsets.getOrDefault(i, 0);
      str.append(String.format("%08X", offset));
    }

    return Base64.encodeBase64String(DigestUtils.sha1(str.toString()))
        .replaceAll("\\+", ".")
        .replaceAll("/", "_")
        .replaceAll("=", "-");
  }

  private int calculateOffset(Metadata track) {
    return track.getCues().stream()
        .filter(cue -> cue.getIndex() == 1)
        .mapToInt(CuePoint::getOffset)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Track does not have cue point with index 1: " + track));
  }

  public List<Metadata> lookupByDiscId(String discId) {
    // TODO use same inc= as on release to save a step
    return getXml("https://musicbrainz.org/ws/2/discid/%s?inc=recordings+artists+release-groups+labels", discId).map(doc -> {
      return XmlDocuments.getChildren(doc, "metadata", "disc", "release-list", "release")
          .map(release -> handleRelease(release, discId))
          .collect(Collectors.toList());
    }).orElse(List.of());
  }

  private Metadata handleRelease(Node release, String discId) {
    var meta = Metadata.builder("generic");
    // TODO duplication
    meta.setSourceFile(cachePath(safeUri("https://musicbrainz.org/ws/2/discid/%s?inc=recordings+artists+release-groups+labels", discId)));
    meta.addTag("musicbrainz_discid", discId);
    
    getText(release, "@id").ifPresent(value -> meta.addTag("musicbrainz_albumid", value));
    getText(release, "title").ifPresent(value -> meta.addTag("album", value));
    getText(release, "date").ifPresent(value -> meta.addTag("date", value));
    getText(release, "barcode").ifPresent(value -> meta.addTag("barcode", value));

    getText(release, "release-group", "@id").ifPresent(value -> meta.addTag("musicbrainz_releasegroupid", value));
    getText(release, "release-group", "first-release-date").ifPresent(value -> meta.addTag("originaldate", value));

    var primaryReleaseType = getText(release, "release-group", "primary-type");
    var secondaryReleaseTypes = XmlDocuments.getChildren(release, "release-group", "secondary-type-list", "secondary-type")
        .map(Node::getTextContent);
    var releaseType = Stream.concat(primaryReleaseType.stream(), secondaryReleaseTypes)
        .collect(Collectors.joining(" + "));

    primaryReleaseType.ifPresent(value -> meta.addTag("releasetype", releaseType));

    getText(release, "metadata", "label-info-list", "label-info", "label", "name").ifPresent(value -> meta.addTag("label", value));
    getText(release, "metadata", "label-info-list", "label-info", "catalog-number").ifPresent(value -> meta.addTag("catalognumber", value));

    XmlDocuments.getChildren(release, "artist-credit", "name-credit", "artist", "@id")
        .map(Node::getTextContent)
        .forEach(value -> meta.addTag("musicbrainz_albumartistid", value));
    XmlDocuments.getChildren(release, "artist-credit", "name-credit", "artist", "name")
        .map(Node::getTextContent)
        .forEach(value -> meta.addTag("albumartist", value));
    XmlDocuments.getChildren(release, "artist-credit", "name-credit", "artist", "sort-name")
        .map(Node::getTextContent)
        .forEach(value -> meta.addTag("albumartistsort", value));

    XmlDocuments.getChildren(release, "medium-list", "medium")
        .forEach(medium -> handleMedium(medium, meta));

    return meta.build();
  }

  private void handleMedium(Node medium, Metadata.Builder parent) {
    var meta = parent.newChild();
    getText(medium, "position").ifPresent(value -> meta.addTag("discnumber", value));

    XmlDocuments.getChildren(medium, "track-list", "track")
        .forEach(track -> handleTrack(track, meta));
  }

  private void handleTrack(Node track, Metadata.Builder parent) {
    var meta = parent.newChild();

    getText(track, "@id").ifPresent(value -> meta.addTag("musicbrainz_trackid", value));
    getText(track, "position").ifPresent(value -> meta.addTag("tracknumber", value));
    getText(track, "recording", "title").ifPresent(value -> meta.addTag("title", value));

    getText(track, "recording", "@id").ifPresent(value -> {
      handleRecording(value, meta);
    });
  }

  private void handleRecording(String recordingId, Metadata.Builder meta) {
    var maybeDoc = getXml("https://musicbrainz.org/ws/2/recording/%s?inc=artists+work-rels", recordingId);
    meta.addTag("musicbrainz_recordingid", recordingId);
    maybeDoc.ifPresent(doc -> {
      XmlDocuments.getChildren(doc, "metadata", "recording", "artist-credit", "name-credit", "artist", "@id")
          .map(Node::getTextContent)
          .forEach(value -> meta.addTag("musicbrainz_artistid", value));
      XmlDocuments.getChildren(doc, "metadata", "recording", "artist-credit", "name-credit", "artist", "name")
          .map(Node::getTextContent)
          .forEach(value -> meta.addTag("artist", value));
      XmlDocuments.getChildren(doc, "metadata", "recording", "artist-credit", "name-credit", "artist", "sort-name")
          .map(Node::getTextContent)
          .forEach(value -> meta.addTag("artistsort", value));

      XmlDocuments.getChildren(doc, "metadata", "recording", "relation-list")
          .filter(rel -> "work".equals(XmlDocuments.getAttribute(rel, "target-type")))
          .findFirst()
          .flatMap(rel -> getText(rel, "relation", "work", "@id"))
          .ifPresent(value -> handleWork(value, meta));
    });
  }

  private void handleWork(String workId, Metadata.Builder meta) {
    meta.addTag("musicbrainz_workid", workId);

    var maybeDoc = getXml("https://musicbrainz.org/ws/2/work/%s?inc=artist-rels", workId);
    if (maybeDoc.isPresent()) {
      var doc = maybeDoc.get();

      XmlDocuments.getChildren(doc, "metadata", "work", "relation-list")
          .filter(relList -> "artist".equals(XmlDocuments.getAttribute(relList, "target-type")))
          .flatMap(relList -> XmlDocuments.getChildren(relList, "relation"))
          .forEach(rel -> handleArtistRel(rel, meta));
    }
  }

  private void handleArtistRel(Node rel, Metadata.Builder meta) {
    var type = XmlDocuments.getAttribute(rel, "type");
    var name = XmlDocuments.getText(rel, "artist", "name");
    var sortName = XmlDocuments.getText(rel, "artist", "sort-name");
    switch (type) {
      case "composer":
        name.ifPresent(value -> meta.addTag("composer", value));
        sortName.ifPresent(value -> meta.addTag("composersort", value));
        break;
      case "lyricist":
        name.ifPresent(value -> meta.addTag("lyricist", value));
        break;
      case "conductor":
        name.ifPresent(value -> meta.addTag("conductor", value));
        break;
      default:
        // TODO log
    }
  }

  private Optional<Document> getXml(String uriFormat, Object... uriArgs) {
    var uri = safeUri(uriFormat, uriArgs);
    var notFoundPath = notFoundPath(uri);
    var cachePath = cachePath(uri);

    if (Files.exists(notFoundPath)) {
      logger.debug("Using cached not found result for URI: {}", uri);
      return Optional.empty();
    } else if (Files.exists(cachePath)) {
      logger.debug("Using cached result for URI: {}", uri);
      return Optional.of(XmlDocuments.parse(cachePath));
    } else {
      rateLimitLock.lock();
      try {
        logger.info("Requesting URI, as it is not cached: {}", uri);
        var request = HttpRequest.newBuilder(uri)
            .GET()
            .header("User-Agent", "mulima/0.2.0-SNAPSHOT ( https://github.com/ajoberstar/mulima )")
            .build();
        var handler = HttpResponse.BodyHandlers.ofInputStream();

        var response = http.send(request, handler);
        if (response.statusCode() == 200) {
          try (var stream = response.body()) {
            var doc = XmlDocuments.parse(stream);
            XmlDocuments.write(doc, cachePath);
            return Optional.of(doc);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        } else if (response.statusCode() == 404) {
          logger.warn("Not found at: {}", uri);
          Files.createFile(notFoundPath);
          return Optional.empty();
        } else {
          // FIXME
          logger.error("Error {} at {}: {}", response.statusCode(), uri, IOUtils.toString(response.body()));
          // TODO do better
          throw new RuntimeException("Something bad: " + response.toString());
        }
      } catch (InterruptedException e) {
        // TODO do better
        throw new RuntimeException("Interrupted.", e);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      } finally {
        try {
          // Musicbrainz as a rate limit. You can only request once per second. So leave a buffer between requests.
          Thread.sleep(1_000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        rateLimitLock.unlock();
      }
    }
  }

  private Path cachePath(URI uri) {
    var uriHash = DigestUtils.sha256Hex(uri.toString());
    // TODO externalize path
    return Paths.get("D:", "temp", "found", uriHash + ".xml");
  }

  private Path notFoundPath(URI uri) {
    var uriHash = DigestUtils.sha256Hex(uri.toString());
    // TODO externalize path
    return Paths.get("D:", "temp", "not-found", uriHash + ".xml");
  }

  private URI safeUri(String format, Object... args) {
    try {
      var str = String.format(format, args);
      return new URI(str);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
