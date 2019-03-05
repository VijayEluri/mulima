package org.mulima.audio.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mulima.file.audio.AudioFile;
import org.mulima.file.audio.AudioFormat;
import org.mulima.proc.ProcessCaller;
import org.mulima.service.MulimaPropertiesSupport;
import org.mulima.util.FileUtil;
import org.springframework.stereotype.Component;

/**
 * Supports Nero AAC encode/decode operations.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Component
public class NeroAacCodec extends MulimaPropertiesSupport implements Codec {
  // private final Logger logger = LogManager.getLogger(getClass());
  private String encPath = "neroAacEnc";
  private String decPath = "neroAacDec";
  private String quality = "0.5";
  private String opts = "";

  @Override
  public List<String> getScope() {
    return Arrays.asList("codec", "aac");
  }

  @Override
  public AudioFormat getFormat() {
    return AudioFormat.AAC;
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

  public String getDecPath() {
    return getProperties().getProperty("decPath", decPath);
  }

  /**
   * Sets the path to the decoder executable.
   *
   * @param decPath the decoder exe path
   */
  public void setDecPath(String decPath) {
    this.decPath = decPath;
  }

  public String getQuality() {
    return getProperties().getProperty("quality", quality);
  }

  /**
   * Sets the quality of the encode.
   *
   * @param quality the quality (0.0-1.0)
   */
  public void setQuality(String quality) {
    this.quality = quality;
  }

  public String getOpts() {
    return getProperties().getProperty("opts", opts);
  }

  /**
   * Sets the additional options to use. These will be used in both encodes and decodes.
   *
   * @param opts the options
   */
  public void setOpts(String opts) {
    this.opts = opts;
  }

  /** {@inheritDoc} */
  @Override
  public CodecResult encode(AudioFile source, AudioFile dest) {
    String sourcePath = FileUtil.getSafeCanonicalPath(source);
    String destPath = FileUtil.getSafeCanonicalPath(dest);

    List<String> command = new ArrayList<String>();
    command.add(getEncPath());
    if (!"".equals(getOpts())) {
      command.add(getOpts());
    }
    command.add("-q");
    command.add(getQuality());
    command.add("-if");
    command.add("\"" + sourcePath + "\"");
    command.add("-of");
    command.add("\"" + destPath + "\"");

    ProcessCaller caller = new ProcessCaller("encoding " + sourcePath, command);
    return new CodecResult(source, dest, caller.call());
  }

  /** {@inheritDoc} */
  @Override
  public CodecResult decode(AudioFile source, AudioFile dest) {
    String sourcePath = FileUtil.getSafeCanonicalPath(source);
    String destPath = FileUtil.getSafeCanonicalPath(dest);

    List<String> command = new ArrayList<String>();
    command.add(getDecPath());
    if (!"".equals(getOpts())) {
      command.add(getOpts());
    }
    command.add("-if");
    command.add(sourcePath);
    command.add("-of");
    command.add(destPath);

    ProcessCaller caller = new ProcessCaller("decoding " + sourcePath, command);
    return new CodecResult(source, dest, caller.call());
  }
}
