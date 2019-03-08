package org.ajoberstar.mulima.service;

import org.ajoberstar.mulima.util.AsyncCollectors;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ajoberstar.mulima.meta.CuePoint;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.util.XmlDocuments;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.ajoberstar.mulima.util.XmlDocuments.getText;

public final class MusicBrainzService {
    private static final Logger logger = LogManager.getLogger(MusicBrainzService.class);

    private final HttpClient http;

    public MusicBrainzService(HttpClient http) {
        this.http = http;
    }

    public CompletionStage<String> calculateDiscId(Metadata cuesheet, Path flacFile) {
        var tracks = cuesheet.denormalize();

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

        var sampleRateStage = ProcessService.executeForOutput("C:\\Users\\andre\\bin\\metaflac.exe", "--show-sample-rate", flacFile.toString())
                .thenApply(String::trim)
                .thenApply(Long::parseLong);
        var sampleTotalStage = ProcessService.executeForOutput("C:\\Users\\andre\\bin\\metaflac.exe", "--show-total-samples", flacFile.toString())
                .thenApply(String::trim)
                .thenApply(Long::parseLong);

        return sampleRateStage.thenCombine(sampleTotalStage, (sampleRate, sampleTotal) -> {
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
        });
    }

    private int calculateOffset(Metadata track) {
        return track.getCues().stream()
                .filter(cue -> cue.getIndex() == 1)
                .mapToInt(CuePoint::getOffset)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Track does not have cue point with index 1: " + track));
    }

    public CompletionStage<List<Metadata>> lookupByDiscId(String discId) {
        return getXml("https://musicbrainz.org/ws/2/discid/%s", discId).thenApply(maybeDoc -> {
            return maybeDoc.map(doc -> {
                return XmlDocuments.getChildren(doc, "metadata", "disc", "release-list", "release")
                        .map(release -> handleRelease(release, discId))
                        .collect(Collectors.toList());
            }).orElse(Collections.emptyList());
        });
    }

    private Metadata handleRelease(Node release, String discId) {
        var meta = Metadata.builder("generic");
        meta.addTag("musicbrainz_discid", discId);
        meta.addTag("musicbrainz_albumid", XmlDocuments.getAttribute(release, "id"));
        getText(release, "title").ifPresent(value -> meta.addTag("album", value));
        getText(release, "date").ifPresent(value -> meta.addTag("date", value));
        getText(release, "barcode").ifPresent(value -> meta.addTag("barcode", value));
        getText(release, "medium-list", "@count").ifPresent(value -> meta.addTag("totaldiscs", value));
        XmlDocuments.getChildren(release, "medium-list", "medium")
                .filter(medium -> {
                    return XmlDocuments.getChildren(medium, "disc-list", "disc")
                            .map(disc -> XmlDocuments.getAttribute(disc, "id"))
                            .anyMatch(discId::equals);
                }).map(medium -> getText(medium, "position"))
                .flatMap(Optional::stream)
                .findFirst()
                .ifPresent(value -> meta.addTag("discnumber", value));

        return meta.build();
    }

    public CompletionStage<Optional<Metadata>> lookupByReleaseId(String releaseId) {
        return getXml("https://musicbrainz.org/ws/2/release/%s?inc=recordings+artists+release-groups+labels", releaseId).thenCompose(maybeDoc -> {
            return maybeDoc.map(doc -> {
                var meta = Metadata.builder("generic");

                getText(doc, "metadata", "release", "@id").ifPresent(value -> meta.addTag("musicbrainz_albumid", value));
                getText(doc, "metadata", "release", "title").ifPresent(value -> meta.addTag("album", value));
                getText(doc, "metadata", "release", "date").ifPresent(value -> meta.addTag("date", value));
                getText(doc, "metadata", "release", "barcode").ifPresent(value -> meta.addTag("barcode", value));
                getText(doc, "metadata", "release", "medium-list", "@count").ifPresent(value -> meta.addTag("totaldiscs", value));

                getText(doc, "metadata", "release", "release-group", "@id").ifPresent(value -> meta.addTag("musicbrainz_releasegroupid", value));
                getText(doc, "metadata", "release", "release-group", "first-release-date").ifPresent(value -> meta.addTag("originaldate", value));

                var primaryReleaseType = getText(doc, "metadata", "release", "release-group", "primary-type");
                var secondaryReleaseTypes = XmlDocuments.getChildren(doc, "metadata", "release", "release-group", "secondary-type-list", "secondary-type")
                        .map(Node::getTextContent)
                        .collect(Collectors.joining(" + "));
                primaryReleaseType.ifPresent(value -> meta.addTag("releasetype", String.join(" + ", value, secondaryReleaseTypes)));

                getText(doc, "metadata", "label-info-list", "label-info", "label", "name").ifPresent(value -> meta.addTag("label", value));
                getText(doc, "metadata", "label-info-list", "label-info", "catalog-number").ifPresent(value -> meta.addTag("catalognumber", value));

                XmlDocuments.getChildren(doc, "metadata", "release", "artist-credit", "name-credit", "artist", "@id")
                        .map(Node::getTextContent)
                        .forEach(value -> meta.addTag("musicbrainz_albumartistid", value));
                XmlDocuments.getChildren(doc, "metadata", "release", "artist-credit", "name-credit", "artist", "name")
                        .map(Node::getTextContent)
                        .forEach(value -> meta.addTag("albumartist", value));
                XmlDocuments.getChildren(doc, "metadata", "release", "artist-credit", "name-credit", "artist", "sort-name")
                        .map(Node::getTextContent)
                        .forEach(value -> meta.addTag("albumartistsort", value));

                var mediums = XmlDocuments.getChildren(doc, "metadata", "release", "medium-list", "medium")
                        .map(medium -> handleMedium(medium, meta))
                        .collect(AsyncCollectors.allOf());

                return mediums.thenApply(ignored -> {
                    return Optional.of(meta.build());
                });
            }).orElse(CompletableFuture.completedStage(Optional.empty()));
        });
    }

    private CompletionStage<Void> handleMedium(Node medium, Metadata.Builder parent) {
        var meta = parent.newChild();
        getText(medium, "position").ifPresent(value -> meta.addTag("discnumber", value));
        getText(medium, "track-list", "@count").ifPresent(value -> meta.addTag("totaltracks", value));

        return XmlDocuments.getChildren(medium, "track-list", "track")
                .map(track -> handleTrack(track, meta))
                .collect(AsyncCollectors.allOf());
    }

    private CompletionStage<Void> handleTrack(Node track, Metadata.Builder parent) {
        var meta = parent.newChild();

        getText(track, "@id").ifPresent(value -> meta.addTag("musicbrainz_trackid", value));
        getText(track, "position").ifPresent(value -> meta.addTag("tracknumber", value));
        getText(track, "recording", "title").ifPresent(value -> meta.addTag("title", value));

        return getText(track, "recording", "@id")
                .map(value -> handleRecording(value, meta))
                .orElse(CompletableFuture.completedStage(null));
    }

    private CompletionStage<Void> handleRecording(String recordingId, Metadata.Builder meta) {
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

            }).orElse(CompletableFuture.completedStage(null));
        });
    }

    private CompletionStage<Void> handleWork(String workId, Metadata.Builder meta) {
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

    private CompletionStage<Optional<Document>> getXml(String uriFormat, Object... uriArgs) {
        var uri = safeUri(uriFormat, uriArgs);

        var uriHash = DigestUtils.sha256Hex(uri.toString());
        // TODO externalize path
        var cachePath = Paths.get("D:", "temp", uriHash + ".xml");
        if (Files.exists(cachePath)) {
            logger.debug("Using cached result for URI: {}", uri);
            return CompletableFuture.completedStage(Optional.of(XmlDocuments.parse(cachePath)));
        } else {
            // TODO lower level
            logger.error("Requesting URI, as it is not cached: {}", uri);
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
                    logger.warn("Not found at: {}", uri);
                    return Optional.empty();
                } else {
                    // TODO do better
                    throw new RuntimeException("Something bad: " + response.toString());
                }
            });
        }
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
