package org.mulima.future.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.future.meta.CuePoint;
import org.mulima.future.meta.Metadata;
import org.mulima.future.util.HttpClients;
import org.mulima.future.util.XmlDocuments;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mulima.future.util.XmlDocuments.getText;

public final class MusicBrainzService {
    private static final Logger logger = LogManager.getLogger(MusicBrainzService.class);

    private final HttpClient http;

    public MusicBrainzService() {
        this.http = HttpClients.rateLimited(1_000);
    }

    public MusicBrainzService(HttpClient http) {
        this.http = http;
    }

    public String calculateDiscId(Metadata cuesheet, Path flacFile) {
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

        var offsets = tracks.stream()
                .collect(Collectors.toMap(trackNum, this::calculateOffset));

        try {
            var sampleRate = Long.parseLong(ProcessService.executeForOutput("C:\\Users\\andre\\bin\\metaflac.exe", "--show-sample-rate", flacFile.toString()).get().trim());
            var sampleTotal = Long.parseLong(ProcessService.executeForOutput("C:\\Users\\andre\\bin\\metaflac.exe", "--show-total-samples", flacFile.toString()).get().trim());
            var leadOutOffset = sampleTotal * 75 / sampleRate + 150;
            offsets.put(0, (int) leadOutOffset);
        } catch (ExecutionException | InterruptedException e) {
            // FIXME better
            throw new RuntimeException(e);
        }

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

    public int calculateOffset(Metadata track) {
        return track.getCues().stream()
                .filter(cue -> cue.getIndex() == 1)
                .mapToInt(CuePoint::getOffset)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Track does not have cue point with index 1: " + track));
    }

    public List<Metadata> lookupByDiscId(String discId) {
        var maybeDoc = getXml("https://musicbrainz.org/ws/2/discid/%s", discId);
        if (maybeDoc.isPresent()) {
            var doc = maybeDoc.get();
            return XmlDocuments.getChildren(doc, "metadata", "disc", "release-list", "release")
                    .map(release -> handleRelease(release, discId))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
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

    public Optional<Metadata> lookupByReleaseId(String releaseId) {
        var maybeDoc = getXml("https://musicbrainz.org/ws/2/release/%s?inc=recordings+artists+release-groups+labels", releaseId);
        if (maybeDoc.isPresent()) {
            var doc = maybeDoc.get();
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

            XmlDocuments.getChildren(doc, "metadata", "release", "medium-list", "medium")
                    .forEach(medium -> handleMedium(medium, meta));

            return Optional.of(meta.build());
        } else {
            return Optional.empty();
        }
    }

    private void handleMedium(Node medium, Metadata.Builder parent) {
        var meta = parent.newChild();
        getText(medium, "position").ifPresent(value -> meta.addTag("discnumber", value));
        getText(medium, "track-list", "@count").ifPresent(value -> meta.addTag("totaltracks", value));

        XmlDocuments.getChildren(medium, "track-list", "track")
                .forEach(track -> handleTrack(track, meta));
    }

    private void handleTrack(Node track, Metadata.Builder parent) {
        var meta = parent.newChild();

        getText(track, "@id").ifPresent(value -> meta.addTag("musicbrainz_trackid", value));
        getText(track, "position").ifPresent(value -> meta.addTag("tracknumber", value));
        getText(track, "recording", "title").ifPresent(value -> meta.addTag("title", value));

        getText(track, "recording", "@id").ifPresent(value -> handleRecording(value, meta));
    }

    private void handleRecording(String recordingId, Metadata.Builder meta) {
        meta.addTag("musicbrainz_recordingid", recordingId);

        var maybeDoc = getXml("https://musicbrainz.org/ws/2/recording/%s?inc=artists+work-rels", recordingId);
        if (maybeDoc.isPresent()) {
            var doc = maybeDoc.get();
            XmlDocuments.getChildren(doc, "metadata", "recording", "relation-list")
                    .filter(rel -> "work".equals(XmlDocuments.getAttribute(rel, "target-type")))
                    .findFirst()
                    .ifPresent(rel -> getText(rel, "relation", "work", "@id").ifPresent(value -> handleWork(value, meta)));

            XmlDocuments.getChildren(doc, "metadata", "recording", "artist-credit", "name-credit", "artist", "@id")
                    .map(Node::getTextContent)
                    .forEach(value -> meta.addTag("musicbrainz_artistid", value));
            XmlDocuments.getChildren(doc, "metadata", "recording", "artist-credit", "name-credit", "artist", "name")
                    .map(Node::getTextContent)
                    .forEach(value -> meta.addTag("artist", value));
            XmlDocuments.getChildren(doc, "metadata", "recording", "artist-credit", "name-credit", "artist", "sort-name")
                    .map(Node::getTextContent)
                    .forEach(value -> meta.addTag("artistsort", value));
        }
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
        try {
            var uri = safeUri(uriFormat, uriArgs);
            // TODO lower level
            logger.error("Requesting: {}", uri);
            var request = HttpRequest.newBuilder(uri)
                    .GET()
                    .header("User-Agent", "mulima/0.2.0-SNAPSHOT ( https://github.com/ajoberstar/mulima )")
                    .build();
            var handler = HttpResponse.BodyHandlers.ofInputStream();

            var response = http.send(request, handler);

            if (response.statusCode() == 200) {
                try (var stream = response.body()) {
                    return Optional.of(XmlDocuments.parse(stream));
                }
            } else if (response.statusCode() == 404) {
                logger.warn("Not found at: {}", uri);
                return Optional.empty();
            } else {
                // TODO do better
                throw new RuntimeException("Something bad: " + response.toString());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            // TODO log
            Thread.currentThread().interrupt();
            return Optional.empty();
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
