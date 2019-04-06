package org.ajoberstar.mulima.meta;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ajoberstar.mulima.service.ProcessService;

public final class Metaflac implements MetadataParser, MetadataWriter {
  private static final Pattern REGEX = Pattern.compile("comment\\[\\d+\\]: (?<tag>.+?)=(?<value>.+)");

  private final String path;
  private final ProcessService process;

  public Metaflac(String path, ProcessService process) {
    this.path = path;
    this.process = process;
  }

  @Override
  public Metadata parse(Path file) {
    List<String> command = new ArrayList<>();
    command.add(path);
    command.add("--list");
    command.add("--block-type=VORBIS_COMMENT");
    command.add(file.toString());

    var result = process.execute(command).assertSuccess();

    var builder = Metadata.builder("vorbis");
    result.getOutput().lines()
        .map(String::trim)
        .map(REGEX::matcher)
        .filter(Matcher::matches)
        .forEach(matcher -> {
          var name = matcher.group("tag");
          var value = matcher.group("value");
          builder.addTag(name, value);
        });
    return builder.build();
  }

  @Override
  public void write(Metadata meta, Path file) {
    List<String> command = new ArrayList<>();
    command.add(path);
    command.add("--remove-all-tags");

    meta.getArtwork().ifPresent(artwork -> {
      command.add(String.format("--import-picture-from=%s", artwork));
    });

    var translated = meta.translateTags("vorbis");
    translated.entrySet().stream()
        .sorted(Comparator.comparing(Map.Entry::getKey))
        .flatMap(entry -> {
          var tag = entry.getKey();
          return entry.getValue().stream()
              .map(value -> String.format("--set-tag=%s=%s", tag, value.replace("\"", "\\\"")));
        }).forEach(command::add);
    command.add(file.toString());

    process.execute(command).assertSuccess();
  }

  public long getSampleRate(Path file) {
    var result = process.execute(path, "--show-sample-rate", file.toString())
        .assertSuccess()
        .getOutput()
        .trim();
    return Long.parseLong(result);
  }

  public long getTotalSamples(Path file) {
    var result = process.execute(path, "--show-total-samples", file.toString())
        .assertSuccess()
        .getOutput()
        .trim();
    return Long.parseLong(result);
  }
}
