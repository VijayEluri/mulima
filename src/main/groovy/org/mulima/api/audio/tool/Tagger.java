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
package org.mulima.api.audio.tool;

import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;

/**
 * A tagger specifies operations to read and write metadata from an audio file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface Tagger {
  AudioFormat getFormat();

  /**
   * Executes a write operation immediately.
   *
   * @param file the file to write to
   * @return a tagger result
   */
  TaggerResult write(AudioFile file);

  /**
   * Executes a read operation immediately.
   *
   * @param file the file to read from
   * @return a tagger result
   */
  TaggerResult read(AudioFile file);
}
