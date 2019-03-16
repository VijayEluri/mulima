package org.ajoberstar.mulima.meta;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CueSheetParser implements MetadataParser {
  private static final Logger logger = LogManager.getLogger(CueSheetParser.class);
  private static final Pattern NUM_REGEX = Pattern.compile(".*\\(([0-9])\\)\\.cue");
  private static final Pattern LINE_REGEX = Pattern.compile("^(?<tag>(?:REM )?[A-Z0-9]+) (?<quote>[\"'])?(?<value>.+?)\\2?(?: WAVE)?$");

  @Override
  public boolean accepts(Path file) {
    return file.getFileName().toString().endsWith(".cue");
  }

  @Override
  public Metadata parse(Path file) {
    try {
      var rootBuilder = Metadata.builder("cuesheet");
      rootBuilder.setSourceFile(file);
      rootBuilder.addTag("DISC", parseDiscNumber(file));

      Metadata.Builder trackBuilder = null;

      for (var line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
        var matcher = LINE_REGEX.matcher(line.trim());
        if (!matcher.find()) {
          logger.warn("Invalid cue sheet line in {}: {}", file, line);
          continue;
        }

        var name = matcher.group("tag").trim();
        var value = matcher.group("value").trim();

        if ("FILE".equals(name)) {
          rootBuilder.setAudioFile(file.resolveSibling(value));
        } else if ("TRACK".equals(name)) {
          trackBuilder = rootBuilder.newChild();
          var currentTrack = Integer.parseInt(value.split(" ")[0]);
          trackBuilder.addTag(name, Integer.toString(currentTrack));
        } else if (trackBuilder == null) {
          var n = "TITLE".equals(name) ? "ALBUM" : name;
          rootBuilder.addTag(n, value);
        } else if ("INDEX".equals(name)) {
          var values = value.split(" ");
          int index = Integer.parseInt(values[0]);
          var time = values[1];
          trackBuilder.addCue(new CuePoint(index, time));
        } else {
          trackBuilder.addTag(name, value);
        }
      }

      return rootBuilder.build();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private String parseDiscNumber(Path file) {
    var matcher = NUM_REGEX.matcher(file.getFileName().toString());
    return matcher.results()
        .map(result -> result.group(1))
        .map(Integer::parseInt)
        .map(Object::toString)
        .findFirst()
        .orElse("1");
  }
}
