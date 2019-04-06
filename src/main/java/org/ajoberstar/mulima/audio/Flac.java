package org.ajoberstar.mulima.audio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ajoberstar.mulima.meta.Album;
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
  public List<Path> split(Album album, List<Metadata> metadata, Path destDir) {
    album.getAudioToCues().forEach((sourceFile, cues) -> {
      var discNum = toDiscNumber(album.getAudioToMetadata().get(sourceFile));
      split(sourceFile, discNum, cues, destDir);
    });
    return tagSplitFiles(destDir, metadata);
  }

  private void split(Path sourceFile, int discNum, List<CuePoint> cues, Path destDir) {
    var onlyOneTrack = cues.stream().map(CuePoint::getTime).allMatch("00:00:00"::equals);
    if (onlyOneTrack) {
      var destFile = destDir.resolve(String.format("D%03dT01.flac", discNum));
      try {
        Files.copy(sourceFile, destFile);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
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
      command.add(destDir.toString());

      // start filenames from num
      command.add("-a");
      command.add(String.format("D%03dT", discNum));

      var startsAtTrack1 = cues.stream()
          .map(CuePoint::getTime)
          .anyMatch("00:00:00"::equals);

      // start num
      command.add("-c");
      command.add(startsAtTrack1 ? "1" : "0");

      // source file
      command.add(sourceFile.toString());

      var input = cues.stream()
          .sorted(Comparator.comparing(CuePoint::getTime))
          .map(cue -> cue.getTime().replaceAll(":([^:\\.]+)$", ".$1"))
          .collect(Collectors.joining(System.lineSeparator()));

      process.execute(command, input).assertSuccess();
    }
  }

  private List<Path> tagSplitFiles(Path directory, List<Metadata> tracks) {
    var metaByDiscAndTrack = tracks.stream()
        .collect(Collectors.groupingBy(this::toDiscNumber, Collectors.toMap(this::toTrackNumber, Function.identity())));

    try (var stream = Files.list(directory)) {
      return stream.flatMap(file -> {
        var matcher = FILE_PATTERN.matcher(file.getFileName().toString());
        if (matcher.matches()) {
          var disc = Integer.parseInt(matcher.group("disc"));
          var track = Integer.parseInt(matcher.group("track"));

          if (track == 0) {
            try {
              Files.delete(file);
              return Stream.empty();
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          } else {
            var trackMeta = metaByDiscAndTrack.get(disc).get(track);
            metaflac.write(trackMeta, file);
            return Stream.of(file);
          }
        } else {
          return Stream.empty();
        }
      }).collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
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
