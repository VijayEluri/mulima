package org.ajoberstar.mulima.meta;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CueSheet {
  private static final Logger logger = LogManager.getLogger(CueSheet.class);
  private static final Pattern LINE_REGEX = Pattern.compile("^\\s*INDEX 01 (?<time>\\d{2}:\\d{2}:\\d{2}\\s*)$");

  public List<CuePoint> parse(Path file) {
    try (var lines = Files.lines(file, StandardCharsets.UTF_8)) {
      // assumes the cue points will be in contiguous order matching the track numbers - 1
      return lines
          .map(LINE_REGEX::matcher)
          .filter(Matcher::find)
          .map(m -> m.group("time"))
          .map(time -> new CuePoint(1, time))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read cue sheet: " + file, e);
    }
  }
}
