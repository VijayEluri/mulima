package org.mulima.audio.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.mulima.file.audio.AudioFile;
import org.mulima.file.audio.AudioFormat;
import org.mulima.meta.ITunesTag;
import org.mulima.meta.Track;
import org.mulima.proc.ProcessCaller;
import org.mulima.service.MulimaPropertiesSupport;
import org.mulima.util.FileUtil;
import org.springframework.stereotype.Component;

/**
 * Support for Nero AAC read/write tag operations.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Component
public class NeroAacTagger extends MulimaPropertiesSupport implements Tagger {
  private static final Pattern REGEX = Pattern.compile("([A-Za-z]+) = (.+)");
  // private final Logger logger = LogManager.getLogger(getClass());
  private String path = "neroAacTag";

  @Override
  protected List<String> getScope() {
    return Arrays.asList("tagger", "aac");
  }

  @Override
  public AudioFormat getFormat() {
    return AudioFormat.AAC;
  }

  public String getPath() {
    return getProperties().getProperty("path", path);
  }

  /**
   * Sets the path to the executable.
   *
   * @param path exe path
   */
  public void setPath(String path) {
    this.path = path;
  }

  /** {@inheritDoc} */
  @Override
  public TaggerResult write(AudioFile file) {
    var filePath = FileUtil.getSafeCanonicalPath(file);

    List<String> command = new ArrayList<>();
    command.add(getPath());
    command.add("\"" + filePath + "\"");
    for (var tag : ITunesTag.values()) {
      for (var value : file.getMeta().getAll(tag)) {
        var preparedValue = value.replaceAll("\"", "\\\\\"");
        command.add("-meta-user:" + tag.toString() + "=" + preparedValue);
      }
    }
    var result = new ProcessCaller(command).call();
    return new TaggerResult(file, result);
  }

  /** {@inheritDoc} */
  @Override
  public TaggerResult read(AudioFile file) {
    var filePath = FileUtil.getSafeCanonicalPath(file);

    List<String> command = new ArrayList<>();
    command.add(getPath());
    command.add(filePath);
    command.add("-list-meta");

    var result =
        new ProcessCaller("tag of " + FileUtil.getSafeCanonicalPath(file), command).call();

    var track = new Track();
    for (var line : result.getOutput().split("\n")) {
      var matcher = REGEX.matcher(line.trim());
      if (matcher.matches()) {
        var name = matcher.group(1).toLowerCase();

        var tag = ITunesTag.valueOf(name);
        track.add(tag, matcher.group(2));
      }
    }

    return new TaggerResult(file, result);
  }
}
