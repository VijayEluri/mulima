package org.ajoberstar.mulima.audio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ajoberstar.mulima.meta.CuePoint;
import org.ajoberstar.mulima.meta.Metadata;
import org.ajoberstar.mulima.service.ProcessResult;
import org.ajoberstar.mulima.service.ProcessService;

public class FlacCodec implements AudioEncoder, AudioDecoder, AudioSplitter {
  private static final Pattern FILE_PATTERN = Pattern.compile("D0*(?<disc>\\d+)T0*(?<track>\\d+).flac");

  private final String flacPath;
  private final int compressionLevel;
  private final String shntoolPath;
  private final ProcessService process;

  public FlacCodec(String flacPath, int compressionLevel, String shntoolPath, ProcessService process) {
    this.flacPath = flacPath;
    this.compressionLevel = compressionLevel;
    this.shntoolPath = shntoolPath;
    this.process = process;
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
  public CompletionStage<Void> encode(Path source, Path destination) {
    var command = new ArrayList<String>();
    command.add(flacPath);
    command.add("-s"); // silent
    command.add("-" + compressionLevel);
    command.add("-o");
    command.add(destination.toString());
    command.add(source.toString());

    return process.execute(command)
        .thenAccept(ProcessResult::assertSuccess);
  }

  @Override
  public CompletionStage<Void> decode(Path source, Path destination) {
    var command = new ArrayList<String>();
    command.add(flacPath);
    command.add("-d"); // decode
    command.add("-s"); // silent
    command.add("-o");
    command.add(destination.toString());
    command.add(source.toString());
    return process.execute(command)
        .thenAccept(ProcessResult::assertSuccess);
  }

  @Override
  public CompletionStage<List<Metadata>> split(Metadata meta, Path source, Path destinationDirectory) {
    var command = new ArrayList<String>();
    command.add(shntoolPath);
    command.add("split");
    command.add("-q"); // quiet output

    // input format
    command.add("-i");
    command.add(String.format("flac %s -c -s -d %%f", flacPath));

    // output format
    command.add("-o");
    command.add(String.format("flac ext=flac %s -s -%d -o %%f -", flacPath, compressionLevel));

    // don't overwrite files
    command.add("-O");
    command.add("never");

    // destination dir
    command.add("-d");
    command.add(destinationDirectory.toString());

    var dMeta = meta.denormalize();

    // assumes all metadata is for one disc
    var discNum = dMeta.stream()
        .map(Metadata::getTags)
        .flatMap(tags -> tags.getOrDefault("discNumber", List.of()).stream())
        .map(Integer::parseInt)
        .findFirst()
        .orElse(1);

    // start filenames from num
    command.add("-a");
    command.add(String.format("D%02dT", discNum));

    Function<Metadata, Stream<CuePoint>> toSplitPoint = track -> track.getCues().stream().filter(cue -> cue.getIndex() == 1);

    var startsAtTrack1 = dMeta.stream()
        .flatMap(toSplitPoint)
        .map(CuePoint::getTime)
        .anyMatch("00:00:00"::equals);

    // start num
    command.add("-c");
    command.add(startsAtTrack1 ? "1" : "0");

    // source file
    command.add(source.toString());

    var input = dMeta.stream()
        .flatMap(toSplitPoint)
        .map(cue -> cue.getTime().replaceAll(":([^:\\.]+)$", ".$1"))
        .collect(Collectors.joining(System.lineSeparator()));

    return process.execute(command, input)
        .thenApply(ProcessResult::assertSuccess)
        .thenApply(result -> parseSplitDir(destinationDirectory));
  }

  private List<Metadata> parseSplitDir(Path directory) {
    try (var stream = Files.list(directory)) {
      return stream.flatMap(file -> {
        var matcher = FILE_PATTERN.matcher(file.getFileName().toString());
        if (matcher.matches()) {
          var disc = matcher.group("disc");
          var track = matcher.group("track");
          var meta = Metadata.builder("generic")
              .setSourceFile(file)
              .addTag("discnumber", disc)
              .addTag("tracknumber", track)
              .build();
          return Stream.of(meta);
        } else {
          return Stream.empty();
        }
      }).collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
