package org.ajoberstar.mulima.service;

import static org.ajoberstar.mulima.util.XmlDocuments.getText;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ajoberstar.mulima.meta.CuePoint;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.util.AsyncCollectors;
import org.ajoberstar.mulima.util.XmlDocuments;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class MusicBrainzService {
  private static final Logger logger = LogManager.getLogger(MusicBrainzService.class);

  private final HttpClient http;
  private final String metaflacPath;
  private final ProcessService process;

  public MusicBrainzService(HttpClient http, String metaflacPath, ProcessService process) {
    this.http = http;
    this.metaflacPath = metaflacPath;
    this.process = process;
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
    return getXml("https://musicbrainz.org/ws/2/discid/%s?inc=recordings+artists+release-groups+labels", discId).thenApply(maybeDoc -> {
      return maybeDoc.map(doc -> {
        return XmlDocuments.getChildren(doc, "metadata", "disc", "release-list", "release")
            .map(release -> handleRelease(release, discId))
            .collect(Collectors.toList());
      }).orElse(Collections.emptyList());
    }).join();
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

    var mediums = XmlDocuments.getChildren(release, "medium-list", "medium")
        .map(medium -> handleMedium(medium, meta))
        .collect(AsyncCollectors.allOf());

    return mediums.thenApply(ignored -> {
      return meta.build();
    }).join();
  }

  private CompletableFuture<Void> handleMedium(Node medium, Metadata.Builder parent) {
    var meta = parent.newChild();
    getText(medium, "position").ifPresent(value -> meta.addTag("discnumber", value));

    return XmlDocuments.getChildren(medium, "track-list", "track")
        .map(track -> handleTrack(track, meta))
        .collect(AsyncCollectors.allOf());
  }

  private CompletableFuture<Void> handleTrack(Node track, Metadata.Builder parent) {
    var meta = parent.newChild();

    getText(track, "@id").ifPresent(value -> meta.addTag("musicbrainz_trackid", value));
    getText(track, "position").ifPresent(value -> meta.addTag("tracknumber", value));
    getText(track, "recording", "title").ifPresent(value -> meta.addTag("title", value));

    return getText(track, "recording", "@id")
        .map(value -> handleRecording(value, meta))
        .orElse(CompletableFuture.completedFuture(null));
  }

  private CompletableFuture<Void> handleRecording(String recordingId, Metadata.Builder meta) {
    return getXml("https://musicbrainz.org/ws/2/recording/%s?inc=artists+work-rels", recordingId).thenCompose(maybeDoc -> {
      meta.addTag("musicbrainz_recordingid", recordingId);
      return maybeDoc.flatMap(doc -> {
        XmlDocuments.getChildren(doc, "metadata", "recording", "artist-credit", "name-credit", "artist", "@id")
            .map(Node::getTextContent)
            .forEach(value -> meta.addTag("musicbrainz_artistid", value));
        XmlDocuments.getChildren(doc, "metadata", "recording", "artist-credit", "name-credit", "artist", "name")
            .map(Node::getTextContent)
            .forEach(value -> meta.addTag("artist", value));
        XmlDocuments.getChildren(doc, "metadata", "recording", "artist-credit", "name-credit", "artist", "sort-name")
            .map(Node::getTextContent)
            .forEach(value -> meta.addTag("artistsort", value));

        return XmlDocuments.getChildren(doc, "metadata", "recording", "relation-list")
            .filter(rel -> "work".equals(XmlDocuments.getAttribute(rel, "target-type")))
            .findFirst()
            .flatMap(rel -> {
              return getText(rel, "relation", "work", "@id")
                  .map(value -> handleWork(value, meta));
            });

      }).orElse(CompletableFuture.completedFuture(null));
    });
  }

  private CompletableFuture<Void> handleWork(String workId, Metadata.Builder meta) {
    meta.addTag("musicbrainz_workid", workId);

    return getXml("https://musicbrainz.org/ws/2/work/%s?inc=artist-rels", workId).thenAccept(maybeDoc -> {
      if (maybeDoc.isPresent()) {
        var doc = maybeDoc.get();

        XmlDocuments.getChildren(doc, "metadata", "work", "relation-list")
            .filter(relList -> "artist".equals(XmlDocuments.getAttribute(relList, "target-type")))
            .flatMap(relList -> XmlDocuments.getChildren(relList, "relation"))
            .forEach(rel -> handleArtistRel(rel, meta));
      }
    });
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

  private CompletableFuture<Optional<Document>> getXml(String uriFormat, Object... uriArgs) {
    var uri = safeUri(uriFormat, uriArgs);
    var cachePath = cachePath(uri);
    if (Files.exists(cachePath)) {
      logger.debug("Using cached result for URI: {}", uri);
      return CompletableFuture.completedFuture(Optional.of(XmlDocuments.parse(cachePath)));
    } else {
      logger.debug("Requesting URI, as it is not cached: {}", uri);
      var request = HttpRequest.newBuilder(uri)
          .GET()
          .header("User-Agent", "mulima/0.2.0-SNAPSHOT ( https://github.com/ajoberstar/mulima )")
          .build();
      var handler = HttpResponse.BodyHandlers.ofInputStream();

      return http.sendAsync(request, handler).thenApply(response -> {
        if (response.statusCode() == 200) {
          try (var stream = response.body()) {
            var doc = XmlDocuments.parse(stream);
            XmlDocuments.write(doc, cachePath);
            return Optional.of(doc);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        } else if (response.statusCode() == 404) {
          logger.debug("Not found at: {}", uri);
          return Optional.empty();
        } else {
          // TODO do better
          throw new RuntimeException("Something bad: " + response.toString());
        }
      });
    }
  }

  private Path cachePath(URI uri) {
    var uriHash = DigestUtils.sha256Hex(uri.toString());
    // TODO externalize path
    return Paths.get("D:", "temp", uriHash + ".xml");
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
