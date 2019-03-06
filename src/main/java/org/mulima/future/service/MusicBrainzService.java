package org.mulima.future.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.mulima.future.meta.CuePoint;
import org.mulima.future.meta.Metadata;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MusicBrainzService {
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
        // TODO implement
        return Collections.emptyList();
    }

    public List<Metadata> lookupByFreeDbId(String freeDbId) {
        // TODO implement
        return Collections.emptyList();
    }
}
