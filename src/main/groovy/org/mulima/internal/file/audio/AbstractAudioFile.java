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
package org.mulima.internal.file.audio;

import java.io.File;

import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;

/**
 * A base implementation of an audio file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public abstract class AbstractAudioFile implements AudioFile {
  private final File file;
  private final AudioFormat format;

  /**
   * Constructs an audio file from the parameters.
   *
   * @param file the file
   */
  public AbstractAudioFile(File file) {
    this.file = file;
    this.format = AudioFormat.valueOf(file);
  }

  /** {@inheritDoc} */
  @Override
  public File getFile() {
    return file;
  }

  /** {@inheritDoc} */
  @Override
  public AudioFormat getFormat() {
    return format;
  }

  @Override
  public String toString() {
    return file.toString();
  }
}
