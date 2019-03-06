package org.mulima.future.meta;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public final class CueSheetParser implements MetadataParser {
  private static final Logger logger = LogManager.getLogger(CueSheetParser.class);
  private static final Pattern NUM_REGEX = Pattern.compile(".*\\(([0-9])\\)\\.cue");
  private static final Pattern LINE_REGEX =
      Pattern.compile("^((?:REM )?[A-Z0-9]+) [\"']?([^\"']*)[\"']?.*$");

  @Override
  public boolean accepts(Path file) {
    return file.getFileName().toString().endsWith(".cue");
  }

  @Override
  public Metadata parse(Path file) {
    try {
      var rootBuilder = Metadata.builder("cuesheet");
      rootBuilder.setFile(file);
      rootBuilder.addTag("DISC", parseDiscNumber(file));

      Metadata.Builder trackBuilder = null;

      for (var line : Files.readAllLines(file)) {
        var matcher = LINE_REGEX.matcher(line.trim());
        if (!matcher.find()) {
          logger.warn("Invalid cue sheet line in {}: {}", file, line);
          continue;
        }

        var name = matcher.group(1).trim();
        var value = matcher.group(2).trim();

        if ("TRACK".equals(name)) {
          trackBuilder = rootBuilder.newChild();
          var currentTrack = Integer.parseInt(value.split(" ")[0]);
          trackBuilder.addTag(name, Integer.toString(currentTrack));
        } else if ("INDEX".equals(name)) {
          var values = value.split(" ");
          int index = Integer.valueOf(values[0]);
          var time = values[1];
          trackBuilder.addCue(new CuePoint(index, time));
        } else if (trackBuilder == null) {
          rootBuilder.addTag(name, value);
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
