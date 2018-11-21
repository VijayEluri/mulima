package org.mulima.internal.meta;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.api.file.FileParser;
import org.mulima.api.meta.CueSheet;
import org.mulima.exception.UncheckedMulimaException;

/**
 * Default parser for cue sheets.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class CueSheetParser implements FileParser<CueSheet> {
  private static final Logger logger = LogManager.getLogger(CueSheetParser.class);
  private static final Pattern NUM_REGEX = Pattern.compile(".*\\(([0-9])\\)\\.cue");
  private static final Pattern LINE_REGEX =
      Pattern.compile("^((?:REM )?[A-Z0-9]+) [\"']?([^\"']*)[\"']?.*$");

  /**
   * Parses a cue sheet file.
   *
   * @param file the file to parse
   * @return the parsed cue sheet
   */
  @Override
  public CueSheet parse(File file) {
    Matcher matcher = NUM_REGEX.matcher(file.getName());
    int num = matcher.find() ? Integer.valueOf(matcher.group(1)) : 1;
    CueSheet cue = new DefaultCueSheet(num);

    try (Scanner fin = new Scanner(file)) {
      int currentTrack = -1;
      while (fin.hasNext()) {
        String line = fin.nextLine().trim();
        matcher = LINE_REGEX.matcher(line);
        if (!matcher.find()) {
          logger.debug("Invalid line: " + line);
        }

        String name = matcher.group(1).trim().replaceAll(" ", "_");
        String value = matcher.group(2).trim();

        if ("TRACK".equals(name)) {
          currentTrack = Integer.valueOf(value.split(" ")[0]);
        } else if (currentTrack < 0) {
          handleCueTag(cue, name, value);
        } else if ("INDEX".equals(name)) {
          handleIndex(cue, currentTrack, value);
        }
      }

      return cue;
    } catch (FileNotFoundException e) {
      throw new UncheckedMulimaException(e);
    }
  }

  /**
   * Handles cue level tags.
   *
   * @param cue the cue to add the tag to
   * @param name the name of the tag
   * @param value the value of the tag
   */
  private void handleCueTag(CueSheet cue, String name, String value) {
    try {
      CueSheetTag.Cue tag = CueSheetTag.Cue.valueOf(name);
      cue.add(tag, value);
    } catch (IllegalArgumentException e) {
      logger.debug(e.getMessage(), e);
    }
  }

  /**
   * Handles cue indices.
   *
   * @param cue the cue to add the index to
   * @param currentTrack the current track being parsed
   * @param value the value of the index
   */
  private void handleIndex(CueSheet cue, int currentTrack, String value) {
    String[] values = value.split(" ");
    int index = Integer.valueOf(values[0]);
    String time = values[1];
    cue.getAllCuePoints().add(new DefaultCuePoint(currentTrack, index, time));
  }
}
