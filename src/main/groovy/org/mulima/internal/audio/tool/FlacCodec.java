/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * Support for FLAC encoding/decoding.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Component
public class FlacCodec extends MulimaPropertiesSupport implements Codec {
  //private final Logger logger = LoggerFactory.getLogger(getClass());
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
    String sourcePath = FileUtil.getSafeCanonicalPath(source);
    String destPath = FileUtil.getSafeCanonicalPath(dest);

    List<String> command = new ArrayList<String>();
    command.add(getPath());
    command.add("-f");
    if (!"".equals(getOpts())) {
      command.add(getOpts());
    }
    command.add("-" + getCompressionLevel());
    command.add("-o");
    command.add(destPath);
    command.add(sourcePath);

    ProcessCaller caller = new ProcessCaller("encoding " + sourcePath, command);
    return new CodecResult(source, dest, caller.call());
  }

  /** {@inheritDoc} */
  @Override
  public CodecResult decode(AudioFile source, AudioFile dest) {
    String sourcePath = FileUtil.getSafeCanonicalPath(source);
    String destPath = FileUtil.getSafeCanonicalPath(dest);

    List<String> command = new ArrayList<String>();
    command.add(getPath());
    command.add("-f");
    if (!"".equals(getOpts())) {
      command.add(getOpts());
    }
    command.add("-d");
    command.add("-o");
    command.add(destPath);
    command.add(sourcePath);

    ProcessCaller caller = new ProcessCaller("decoding " + sourcePath, command);
    return new CodecResult(source, dest, caller.call());
  }
}
