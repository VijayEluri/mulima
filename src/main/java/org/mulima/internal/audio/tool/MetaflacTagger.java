package org.mulima.internal.audio.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.audio.tool.Tagger;
import org.mulima.api.audio.tool.TaggerResult;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.meta.Track;
import org.mulima.api.proc.ProcessResult;
import org.mulima.internal.meta.DefaultTrack;
import org.mulima.internal.meta.VorbisTag;
import org.mulima.internal.proc.ProcessCaller;
import org.mulima.internal.service.MulimaPropertiesSupport;
import org.mulima.util.FileUtil;
import org.springframework.stereotype.Component;

/**
 * Support for reading and writing tags via Metaflac.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Component
public class MetaflacTagger extends MulimaPropertiesSupport implements Tagger {
  private static final Pattern REGEX = Pattern.compile("comment\\[[0-9]+\\]: ([A-Za-z]+)=(.+)");
  // private final Logger logger = LogManager.getLogger(getClass());
  private String path = "metaflac";
  private String opts = "";

  public AudioFormat getFormat() {
    return AudioFormat.FLAC;
  }

  @Override
  protected List<String> getScope() {
    return Arrays.asList("tagger", "flac");
  }

  /**
   * Gets the path to the metaflac executable.
   *
   * @return the path to the exe
   */
  public String getPath() {
    return getProperties().getProperty("path", path);
  }

  /**
   * Sets the path to the metaflac executable.
   *
   * @param path the path to the exe
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Gets the additional options to use.
   *
   * @return the options
   */
  public String getOpts() {
    return getProperties().getProperty("opts", opts);
  }

  /**
   * Sets additional options for this codec. These will be used on both reads and writes.
   *
   * @param opts the options
   */
  public void setOpts(String opts) {
    this.opts = opts;
  }

  /** {@inheritDoc} */
  @Override
  public TaggerResult write(AudioFile file) {
    String filePath = FileUtil.getSafeCanonicalPath(file);

    List<String> command = new ArrayList<String>();
    command.add(getPath());
    if (!"".equals(getOpts())) {
      command.add(getOpts());
    }
    command.add("--remove-all-tags");
    for (VorbisTag tag : VorbisTag.values()) {
      for (String value : file.getMeta().getAll(tag)) {
        String preparedValue = value.replaceAll("\"", "\\\\\"");
        command.add("--set-tag=" + tag.toString() + "=" + preparedValue + "");
      }
    }
    command.add("\"" + filePath + "\"");

    ProcessResult result = new ProcessCaller(command).call();
    return new TaggerResult(file, result);
  }

  /** {@inheritDoc} */
  @Override
  public TaggerResult read(AudioFile file) {
    String filePath = FileUtil.getSafeCanonicalPath(file);

    List<String> command = new ArrayList<String>();
    command.add(getPath());
    if (!"".equals(getOpts())) {
      command.add(getOpts());
    }
    command.add("--list");
    command.add("--block-type=VORBIS_COMMENT");
    command.add(filePath);

    ProcessResult result =
        new ProcessCaller("tag of " + FileUtil.getSafeCanonicalPath(file), command).call();

    Track track = new DefaultTrack();
    for (String line : result.getOutput().split("\n")) {
      Matcher matcher = REGEX.matcher(line.trim());
      if (matcher.matches()) {
        String name = matcher.group(1).toUpperCase();

        VorbisTag tag = VorbisTag.valueOf(name);
        if (tag != null) {
          track.add(tag, matcher.group(2));
        }
      }
    }

    return new TaggerResult(file, result);
  }
}
