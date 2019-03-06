package org.mulima.future.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.mulima.future.meta.CuePoint;
import org.mulima.future.meta.Metadata;
import org.mulima.future.util.XmlDocuments;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.swing.plaf.multi.MultiScrollBarUI;
import javax.swing.text.html.parser.Parser;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class MusicBrainzService {
    private final HttpClient http;

    public MusicBrainzService() {
        this.http = HttpClient.newBuilder()
                .executor(Executors.newSingleThreadExecutor())
                .build();
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
        XmlDocuments.getText(release, "title").ifPresent(value -> meta.addTag("album", value));
        XmlDocuments.getText(release, "date").ifPresent(value -> meta.addTag("date", value));
        XmlDocuments.getText(release, "barcode").ifPresent(value -> meta.addTag("barcode", value));
        XmlDocuments.getText(release, "medium-list", "@count").ifPresent(value -> meta.addTag("totaldiscs", value));
        XmlDocuments.getChildren(release, "medium-list", "medium")
                .filter(medium -> {
                    return XmlDocuments.getChildren(medium, "disc-list", "disc")
                            .map(disc -> XmlDocuments.getAttribute(disc, "id"))
                            .anyMatch(discId::equals);
                }).map(medium -> XmlDocuments.getText(medium, "position"))
                .flatMap(Optional::stream)
                .findFirst()
                .ifPresent(value -> meta.addTag("discnumber", value));

        return meta.build();
    }

    private Optional<Document> getXml(String uriFormat, Object... uriArgs) {
        try {
            var uri = safeUri(uriFormat, uriArgs);
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
