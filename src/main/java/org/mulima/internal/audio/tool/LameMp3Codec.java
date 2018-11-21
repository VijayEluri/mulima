package org.mulima.internal.audio.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mulima.api.audio.tool.Codec;
import org.mulima.api.audio.tool.CodecResult;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.internal.proc.ProcessCaller;
import org.mulima.internal.service.MulimaPropertiesSupport;
import org.mulima.util.FileUtil;
import org.springframework.stereotype.Component;

/**
 * Supports Nero AAC encode/decode operations.
 */
@Component
public class LameMp3Codec extends MulimaPropertiesSupport implements Codec {
  // private final Logger logger = LogManager.getLogger(getClass());
  private String encPath = "lame";
  private String bitrate = "320";

  @Override
  public List<String> getScope() {
    return Arrays.asList("codec", "mp3");
  }

  @Override
  public AudioFormat getFormat() {
    return AudioFormat.MP3;
  }

  public String getEncPath() {
    return getProperties().getProperty("encPath", encPath);
  }

  /**
   * Sets the path to the encoder executable.
   *
   * @param encPath the encoder exe path
   */
  public void setEncPath(String encPath) {
    this.encPath = encPath;
  }

  public String getBitrate() {
    return getProperties().getProperty("quality", bitrate);
  }

  /**
   * Sets the bitrate of the encode
   *
   * @param bitrate of the encoding
   */
  public void setBitrate(String bitrate) {
    this.bitrate = bitrate;
  }

  /** {@inheritDoc} */
  @Override
  public CodecResult encode(AudioFile source, AudioFile dest) {
    String sourcePath = FileUtil.getSafeCanonicalPath(source);
    String destPath = FileUtil.getSafeCanonicalPath(dest);

    List<String> command = new ArrayList<String>();
    command.add(getEncPath());
    command.add("-b");
    command.add(getBitrate());
    command.add("\"" + sourcePath + "\"");
    command.add("\"" + destPath + "\"");

    ProcessCaller caller = new ProcessCaller("encoding " + sourcePath, command);
    return new CodecResult(source, dest, caller.call());
  }

  /** {@inheritDoc} */
  @Override
  public CodecResult decode(AudioFile source, AudioFile dest) {
    throw new UnsupportedOperationException("Cannot decode MP3s.");
  }
}
