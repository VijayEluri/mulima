package org.ajoberstar.mulima.service;

import org.ajoberstar.mulima.meta.CuePoint;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.meta.Metaflac;
import org.ajoberstar.mulima.util.XmlDocuments;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ajoberstar.mulima.util.XmlDocuments.getText;

public final class MusicBrainzService {
  private static final Logger logger = LogManager.getLogger(MusicBrainzService.class);

  private final HttpClient http;
  private final Metaflac metaflac;
  private final Path cachePath;

  private final ReentrantLock rateLimitLock;

  public MusicBrainzService(HttpClient http, Metaflac metaflac, Path cachePath) {
    this.http = http;
    this.metaflac = metaflac;
    this.cachePath = cachePath;
    this.rateLimitLock = new ReentrantLock();
  }

  public String calculateDiscId(Path flacFile, List<CuePoint> cues) {
    var firstTrack = 1;
    var lastTrack = cues.size();

    var sampleRate = metaflac.getSampleRate(flacFile);
    var sampleTotal = metaflac.getTotalSamples(flacFile);

    var cueOffsets = cues.stream().map(CuePoint::getOffset);
    var leadOutOffset = sampleTotal * 75 / sampleRate + 150;
    var offsets = Stream.concat(Stream.of(leadOutOffset), cueOffsets).collect(Collectors.toList());

    var str = new StringBuilder();
    str.append(String.format("%02X", firstTrack));
    str.append(String.format("%02X", lastTrack));
    for (var i = 0; i < 100; i++) {
      var offset = i < offsets.size() ? offsets.get(i) : 0;
      str.append(String.format("%08X", offset));
    }

    return Base64.encodeBase64String(DigestUtils.sha1(str.toString()))
        .replaceAll("\\+", ".")
        .replaceAll("/", "_")
        .replaceAll("=", "-");
  }

  public List<String> lookupByDiscId(String discId) {
    var uri = safeUri("https://musicbrainz.org/ws/2/discid/%s", discId);
    return getXml(uri).stream().flatMap(doc -> {
      return XmlDocuments.getChildren(doc, "metadata", "disc", "release-list", "release", "@id")
          .map(Node::getTextContent);
    }).collect(Collectors.toList());
  }

  public Optional<List<Metadata>> lookupByReleaseId(String releaseId) {
    var uri = safeUri("https://musicbrainz.org/ws/2/release/%s?inc=artists+artist-rels+artist-credits+recordings+recording-level-rels+work-rels+work-level-rels+release-groups+release-group-rels+labels+genres+aliases", releaseId);
    return getXml(uri).map(doc -> {
      return XmlDocuments.getChildren(doc, "metadata", "release")
          .flatMap(release -> handleRelease(release))
          .collect(Collectors.toList());
    });
  }

  private Stream<Metadata> handleRelease(Node release) {
    var builder = Metadata.builder("generic");

    getText(release, "@id").ifPresent(value -> builder.addTag("musicbrainz_releaseid", value));
    getText(release, "title").ifPresent(value -> builder.addTag("album", value)); // TODO disambiguation
    getText(release, "date").ifPresent(value -> builder.addTag("date", value));
    getText(release, "barcode").ifPresent(value -> builder.addTag("barcode", value));

    getText(release, "release-group", "@id").ifPresent(value -> builder.addTag("musicbrainz_releasegroupid", value));
    getText(release, "release-group", "first-release-date").ifPresent(value -> builder.addTag("originaldate", value));

    var primaryReleaseType = getText(release, "release-group", "primary-type");
    var secondaryReleaseTypes = XmlDocuments.getChildren(release, "release-group", "secondary-type-list", "secondary-type")
        .map(Node::getTextContent);
    var releaseType = Stream.concat(primaryReleaseType.stream(), secondaryReleaseTypes)
        .collect(Collectors.joining(" + "));

    primaryReleaseType.ifPresent(value -> builder.addTag("releasetype", releaseType));

    getText(release, "metadata", "label-info-list", "label-info", "label", "name").ifPresent(value -> builder.addTag("label", value));
    getText(release, "metadata", "label-info-list", "label-info", "catalog-number").ifPresent(value -> builder.addTag("catalognumber", value));

    // TODO genre

    XmlDocuments.getChildren(release, "artist-credit")
        .forEach(credit -> handleAlbumArtistCredit(credit, builder));

    var meta = builder.build();
    return XmlDocuments.getChildren(release, "medium-list", "medium")
        .flatMap(medium -> handleMedium(medium, meta.copy()));
  }

  private Stream<Metadata> handleMedium(Node medium, Metadata.Builder builder) {
    getText(medium, "title").ifPresent(value -> builder.addTag("discsubtitle", value));
    getText(medium, "position").ifPresent(value -> builder.addTag("discnumber", value));
    var meta = builder.build();
    return XmlDocuments.getChildren(medium, "track-list", "track")
        .map(track -> handleTrack(track, meta.copy()));
  }

  private Metadata handleTrack(Node track, Metadata.Builder builder) {
    getText(track, "@id").ifPresent(value -> builder.addTag("musicbrainz_trackid", value));
    getText(track, "position").ifPresent(value -> builder.addTag("tracknumber", value));
    getText(track, "recording", "title").ifPresent(value -> builder.addTag("title", value)); // TODO use work title?

    // TODO this or the recording artist?
//    XmlDocuments.getChildren(track, "artist-credit")
//        .forEach(credit -> handleArtistCredit(credit, builder));

    XmlDocuments.getChildren(track, "recording").forEach(recording -> {
      handleRecording(recording, builder);
    });
    return builder.build();
  }

  private void handleRecording(Node recording, Metadata.Builder builder) {
    getText(recording, "@id").ifPresent(value -> builder.addTag("musicbrainz_recordingid", value));
    XmlDocuments.getChildren(recording, "artist-credit")
        .forEach(credit -> handleArtistCredit(credit, builder));

    XmlDocuments.getChildren(recording, "relation-list")
        .filter(relList -> "artist".equals(XmlDocuments.getAttribute(relList, "target-type")))
        .flatMap(relList -> XmlDocuments.getChildren(relList, "relation"))
        .forEach(rel -> handleArtistRel(rel, builder));

    XmlDocuments.getChildren(recording, "relation-list")
        .filter(rel -> "work".equals(XmlDocuments.getAttribute(rel, "target-type")))
        .flatMap(workRel -> XmlDocuments.getChildren(workRel, "work"))
        .forEach(work -> handleWork(work, builder));
  }

  private void handleWork(Node work, Metadata.Builder builder) {
    getText(work, "@id").ifPresent(value -> builder.addTag("musicbrainz_workid", value));

    XmlDocuments.getChildren(work, "relation-list")
        .filter(relList -> "artist".equals(XmlDocuments.getAttribute(relList, "target-type")))
        .flatMap(relList -> XmlDocuments.getChildren(relList, "relation"))
        .forEach(rel -> handleArtistRel(rel, builder));

    // TODO identify parent works for movements and movement numbers?
  }

  private void handleArtistRel(Node rel, Metadata.Builder meta) {
    var type = XmlDocuments.getAttribute(rel, "type");
    var name = XmlDocuments.getText(rel, "target-credit").or(() -> XmlDocuments.getText(rel, "artist", "name"));
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

  private void handleAlbumArtistCredit(Node credit, Metadata.Builder meta) {
    XmlDocuments.getChildren(credit, "name-credit")
        .flatMap(nameCredit -> XmlDocuments.getText(nameCredit, "artist", "@id").stream())
        .forEach(value -> meta.addTag("musicbrainz_albumartistid", value));

    var joinedName = XmlDocuments.getChildren(credit, "name-credit")
        .map(nameCredit -> {
          var name = XmlDocuments.getChildren(nameCredit, "artist", "alias-list", "alias")
              .filter(alias -> "primary".equals(XmlDocuments.getText(alias, "@primary").orElse("notprimary")))
              .filter(alias -> "en".equals(XmlDocuments.getText(alias, "@locale").orElse("noten")))
              .map(Node::getTextContent)
              .findFirst()
              .or(() -> XmlDocuments.getText(nameCredit, "artist", "name"))
              .orElse("Unknown");
          var join = XmlDocuments.getText(nameCredit, "@joinphrase").orElse("");
          return name + join;
        }).collect(Collectors.joining());

    meta.addTag("albumartist", joinedName);

    var joinedSortName = XmlDocuments.getChildren(credit, "name-credit")
        .map(nameCredit -> {
          var sortName = XmlDocuments.getChildren(nameCredit, "artist", "alias-list", "alias")
              .filter(alias -> "primary".equals(XmlDocuments.getText(alias, "@primary").orElse("notprimary")))
              .filter(alias -> "en".equals(XmlDocuments.getText(alias, "@locale").orElse("noten")))
              .map(alias -> XmlDocuments.getAttribute(alias, "sort-name"))
              .findFirst()
              .or(() -> XmlDocuments.getText(nameCredit, "artist",  "sort-name"))
              .orElse("Unknown");
          var join = XmlDocuments.getText(nameCredit, "@joinphrase").orElse("");
          return sortName + join;
        }).collect(Collectors.joining());

    meta.addTag("albumartistsort", joinedName);
  }

  private void handleArtistCredit(Node credit, Metadata.Builder meta) {
    XmlDocuments.getChildren(credit, "name-credit")
        .flatMap(nameCredit -> XmlDocuments.getText(nameCredit, "artist", "@id").stream())
        .forEach(value -> meta.addTag("musicbrainz_artistid", value));

    var joinedName = XmlDocuments.getChildren(credit, "name-credit")
        .map(nameCredit -> {
          var name = XmlDocuments.getChildren(nameCredit, "artist", "alias-list", "alias")
              .filter(alias -> "primary".equals(XmlDocuments.getText(alias, "@primary").orElse("notprimary")))
              .filter(alias -> "en".equals(XmlDocuments.getText(alias, "@locale").orElse("noten")))
              .map(Node::getTextContent)
              .findFirst()
              .or(() -> XmlDocuments.getText(nameCredit, "artist", "name"))
              .orElse("Unknown");
          var join = XmlDocuments.getText(nameCredit, "@joinphrase").orElse("");
          return name + join;
        }).collect(Collectors.joining());

    meta.addTag("artist", joinedName);

    var joinedSortName = XmlDocuments.getChildren(credit, "name-credit")
        .map(nameCredit -> {
          var sortName = XmlDocuments.getChildren(nameCredit, "artist", "alias-list", "alias")
              .filter(alias -> "primary".equals(XmlDocuments.getText(alias, "@primary").orElse("notprimary")))
              .filter(alias -> "en".equals(XmlDocuments.getText(alias, "@locale").orElse("noten")))
              .map(alias -> XmlDocuments.getAttribute(alias, "sort-name"))
              .findFirst()
              .or(() -> XmlDocuments.getText(nameCredit, "artist", "sort-name"))
              .orElse("Unknown");
          var join = XmlDocuments.getText(nameCredit, "@joinphrase").orElse("");
          return sortName + join;
        }).collect(Collectors.joining());

    meta.addTag("artistsort", joinedName);
  }

  private Optional<Document> getXml(String uriFormat, Object... uriArgs) {
    var uri = safeUri(uriFormat, uriArgs);
    return getXml(uri);
  }

  private Optional<Document> getXml(URI uri) {
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
            .header("User-Agent", "mulima/0.3.0-SNAPSHOT ( https://github.com/ajoberstar/mulima )")
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
          var streamStr = new ByteArrayOutputStream();
          response.body().transferTo(streamStr);
          logger.error("Error {} at {}: {}", response.statusCode(), uri, streamStr.toString());
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
          // Musicbrainz as a rate limit. You can only request once per second. So leave a buffer between
          // requests.
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
    return cachePath.resolve("found").resolve(uriHash + ".xml");
  }

  private Path notFoundPath(URI uri) {
    var uriHash = DigestUtils.sha256Hex(uri.toString());
    return cachePath.resolve("not-found").resolve(uriHash + ".xml");
  }

  private URI safeUri(String format, Object... args) {
    var str = String.format(format, args);
    return URI.create(str);
  }
}
