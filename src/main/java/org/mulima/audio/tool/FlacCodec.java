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
 * Support for FLAC encoding/decoding.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Component
public class FlacCodec extends MulimaPropertiesSupport implements Codec {
  // private final Logger logger = LogManager.getLogger(getClass());
  private String path = "flac";
  private String opts = "";
  private String compressionLevel = "5";

  @Override
  protected List<String> getScope() {
    return Arrays.asList("codec", "flac");
  }

  @Override
  public AudioFormat getFormat() {
    return AudioFormat.FLAC;
  }

  /**
   * Gets the path to the FLAC executable.
   *
   * @return the path to the exe
   */
  public String getPath() {
    return getProperties().getProperty("path", path);
  }

  /**
   * Sets the path to the FLAC executable.
   *
   * @param path the path to the exe
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Gets the additional options for this codec.
   *
   * @return the options
   */
  public String getOpts() {
    return getProperties().getProperty("opts", opts);
  }

  /**
   * Sets additional options for this codec. Will be used on both encodes and decodes.
   *
   * @param opts the options
   */
  public void setOpts(String opts) {
    this.opts = opts;
  }

  public String getCompressionLevel() {
    return getProperties().getProperty("compressionLevel", compressionLevel);
  }

  /**
   * Sets the compression level for encodes.
   *
   * @param compressionLevel the compression level (1-8)
   */
  public void setCompressionLevel(String compressionLevel) {
    this.compressionLevel = compressionLevel;
  }

  /** {@inheritDoc} */
  @Override
  public CodecResult encode(AudioFile source, AudioFile dest) {
    var sourcePath = FileUtil.getSafeCanonicalPath(source);
    var destPath = FileUtil.getSafeCanonicalPath(dest);

    List<String> command = new ArrayList<>();
    command.add(getPath());
    command.add("-f");
    if (!"".equals(getOpts())) {
      command.add(getOpts());
    }
    command.add("-" + getCompressionLevel());
    command.add("-o");
    command.add("\"" + destPath + "\"");
    command.add("\"" + sourcePath + "\"");

    var caller = new ProcessCaller("encoding " + sourcePath, command);
    return new CodecResult(source, dest, caller.call());
  }

  /** {@inheritDoc} */
  @Override
  public CodecResult decode(AudioFile source, AudioFile dest) {
    var sourcePath = FileUtil.getSafeCanonicalPath(source);
    var destPath = FileUtil.getSafeCanonicalPath(dest);

    List<String> command = new ArrayList<>();
    command.add(getPath());
    command.add("-f");
    if (!"".equals(getOpts())) {
      command.add(getOpts());
    }
    command.add("-d");
    command.add("-o");
    command.add(destPath);
    command.add(sourcePath);

    var caller = new ProcessCaller("decoding " + sourcePath, command);
    return new CodecResult(source, dest, caller.call());
  }
}
