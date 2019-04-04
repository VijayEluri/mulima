package org.ajoberstar.mulima.audio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ajoberstar.mulima.meta.CuePoint;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.meta.Metaflac;
import org.ajoberstar.mulima.service.ProcessService;

public class Flac implements AudioEncoder, AudioDecoder, AudioSplitter {
  private static final Pattern FILE_PATTERN = Pattern.compile("D0*(?<disc>\\d+)T0*(?<track>\\d+).flac");

  private final String flacPath;
  private final int compressionLevel;
  private final String shntoolPath;
  private final ProcessService process;
  private final Metaflac metaflac;

  public Flac(String flacPath, int compressionLevel, String shntoolPath, ProcessService process, Metaflac metaflac) {
    this.flacPath = flacPath;
    this.compressionLevel = compressionLevel;
    this.shntoolPath = shntoolPath;
    this.process = process;
    this.metaflac = metaflac;
  }

  @Override
  public boolean acceptsEncode(Path source, Path destination) {
    return source.getFileName().toString().endsWith(".wav")
        && destination.getFileName().toString().endsWith(".flac");
  }

  @Override
  public boolean acceptsDecode(Path source, Path destination) {
    return source.getFileName().toString().endsWith(".flac")
        && destination.getFileName().toString().endsWith(".wav");
  }

  @Override
  public boolean acceptsSplit(Path source) {
    return source.getFileName().toString().endsWith(".flac");
  }

  @Override
  public void encode(Path source, Path destination) {
    var command = new ArrayList<String>();
    command.add(flacPath);
    command.add("-s"); // silent
    command.add("-" + compressionLevel);
    command.add("-o");
    command.add(destination.toString());
    command.add(source.toString());

    process.execute(command).assertSuccess();
  }

  @Override
  public void decode(Path source, Path destination) {
    var command = new ArrayList<String>();
    command.add(flacPath);
    command.add("-d"); // decode
    command.add("-s"); // silent
    command.add("-o");
    command.add(destination.toString());
    command.add(source.toString());
    process.execute(command).assertSuccess();
  }

  @Override
  public Metadata split(Metadata meta, Path destDir) {
//    var tracksByDisc = meta.getChildren().stream()
//        .collect(Collectors.groupingBy(this::toDiscNumber));
//
//    tracksByDisc.values().forEach(tracks -> {
//      var audioFiles = tracks.stream()
//          .map(Metadata::getAudioFile)
//          .flatMap(Optional::stream)
//          .collect(Collectors.toSet());
//
//      if (audioFiles.size() == 1) {
//        split(tracks, audioFiles.iterator().next(), destDir);
//      } else {
//        throw new IllegalArgumentException("Disc's tracks are source from different audio files: " + tracks);
//      }
//    });
//
//    return tagSplitFiles(meta, destDir);
    return null;
  }

  private void split(List<Metadata> tracks, Path sourceFile, Path destDir) {
//    var command = new ArrayList<String>();
//    command.add(shntoolPath);
//    command.add("split");
//    command.add("-q"); // quiet output
//
//    // input format
//    command.add("-i");
//    command.add(String.format("flac %s -c -s -d %%f", flacPath));
//
//    // output format
//    command.add("-o");
//    command.add(String.format("flac ext=flac %s -s -%d -o %%f -", flacPath, compressionLevel));
//
//    // don't overwrite files
//    command.add("-O");
//    command.add("never");
//
//    // destination dir
//    command.add("-d");
//    command.add(destDir.toString());
//
//    // assumes all metadata is for one disc
//    var discNum = tracks.stream()
//        .map(Metadata::getTags)
//        .flatMap(tags -> tags.getOrDefault("discnumber", List.of()).stream())
//        .map(Integer::parseInt)
//        .findFirst()
//        .orElse(1);
//
//    // start filenames from num
//    command.add("-a");
//    command.add(String.format("D%02dT", discNum));
//
//    Function<Metadata, Stream<CuePoint>> toSplitPoint = track -> track.getCues().stream().filter(cue -> cue.getIndex() == 1);
//
//    var startsAtTrack1 = tracks.stream()
//        .flatMap(toSplitPoint)
//        .map(CuePoint::getTime)
//        .anyMatch("00:00:00"::equals);
//
//    // start num
//    command.add("-c");
//    command.add(startsAtTrack1 ? "1" : "0");
//
//    // source file
//    command.add(sourceFile.toString());
//
//    var input = tracks.stream()
//        .flatMap(toSplitPoint)
//        .sorted(Comparator.comparing(CuePoint::getTime))
//        .map(cue -> cue.getTime().replaceAll(":([^:\\.]+)$", ".$1"))
//        .collect(Collectors.joining(System.lineSeparator()));
//
//    process.execute(command, input).assertSuccess();
  }

  private Metadata tagSplitFiles(Metadata meta, Path directory) {
//    var metaByDiscAndTrack = meta.getChildren().stream()
//        .collect(Collectors.groupingBy(this::toDiscNumber, Collectors.toMap(this::toTrackNumber, Function.identity())));
//
//    try (var stream = Files.list(directory)) {
//      var builder = Metadata.builder("generic");
//      builder.setSourceFile(directory);
//
//      stream.flatMap(file -> {
//        var matcher = FILE_PATTERN.matcher(file.getFileName().toString());
//        if (matcher.matches()) {
//          var disc = Integer.parseInt(matcher.group("disc"));
//          var track = Integer.parseInt(matcher.group("track"));
//
//          if (track == 0) {
//            try {
//              Files.delete(file);
//              return Stream.empty();
//            } catch (IOException e) {
//              throw new UncheckedIOException(e);
//            }
//          } else {
//            var trackMeta = metaByDiscAndTrack.get(disc).get(track);
//            metaflac.write(trackMeta, file);
//            return Stream.of(metaflac.parse(file));
//          }
//        } else {
//          return Stream.empty();
//        }
//      }).forEach(builder::addChild);
//
//      return builder.build();
//    } catch (IOException e) {
//      throw new UncheckedIOException(e);
//    }
    return null;
  }

  private Integer toDiscNumber(Metadata meta) {
    return meta.getTagValue("discnumber")
        .map(Integer::parseInt)
        .orElseThrow(() -> new IllegalArgumentException("Track does not have disc number: " + meta));
  }

  private Integer toTrackNumber(Metadata meta) {
    return meta.getTagValue("tracknumber")
        .map(Integer::parseInt)
        .orElseThrow(() -> new IllegalArgumentException("Track does not have track number: " + meta));
  }
}
