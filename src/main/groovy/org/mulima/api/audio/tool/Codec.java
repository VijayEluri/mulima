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
 * A codec specifies operations for encoding and decoding an audio file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface Codec {
  /**
   * Gets the format supported by this codec.
   *
   * @return the supported format
   */
  AudioFormat getFormat();

  /**
   * Execute an encode operation immediately.
   *
   * @param source the file to encode
   * @param dest the destination for the encoded file
   * @return a codec result
   */
  CodecResult encode(AudioFile source, AudioFile dest);

  /**
   * Execute a decode operation immediately.
   *
   * @param source the file to decode
   * @param dest the destination for the decoded file
   * @return a codec result
   */
  CodecResult decode(AudioFile source, AudioFile dest);
}
