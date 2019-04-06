package org.ajoberstar.mulima.meta;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ajoberstar.mulima.service.ProcessService;

public final class OpusInfo implements MetadataParser {
  private static final Pattern TAG_REGEX = Pattern.compile("(?<tag>.+?)=(?<value>.*)");
  private static final Pattern PLAYBACK_REGEX = Pattern.compile("Playback length: (?<minutes>\\d+)m:(?<seconds>\\d+)\\.(?<millis>\\d+)s");

  private final String path;
  private final ProcessService process;

  public OpusInfo(String path, ProcessService process) {
    this.path = path;
    this.process = process;
  }

  @Override
  public Metadata parse(Path file) {
    List<String> command = new ArrayList<>();
    command.add(path);
    command.add(file.toString());
    var result = process.execute(command).assertSuccess();

    var builder = Metadata.builder("vorbis");
    result.getOutput(StandardCharsets.UTF_8).lines()
        .map(String::trim)
        .dropWhile(line -> !"User comments section follows...".equals(line))
        .skip(1)
        .map(TAG_REGEX::matcher)
        .takeWhile(Matcher::matches)
        .forEach(matcher -> {
          var name = matcher.group("tag");
          var value = matcher.group("value");
          builder.addTag(name, value);
        });
    return builder.build();
  }

  public long getTotalFrames(Path file) {
    List<String> command = new ArrayList<>();
    command.add(path);
    command.add(file.toString());
    var result = process.execute(command).assertSuccess();
    var matcher = PLAYBACK_REGEX.matcher(result.getOutput());
    if (matcher.find()) {
      var minutes = Long.parseLong(matcher.group("minutes"));
      var seconds = Long.parseLong(matcher.group("seconds"));
      var millis = Long.parseLong(matcher.group("millis"));

      var frames = Math.round((double) millis * 75 / 1000);
      return (minutes * 60 + seconds) * 75 + frames;
    } else {
      throw new RuntimeException("Could not identify total frames.");
    }
  }
}
