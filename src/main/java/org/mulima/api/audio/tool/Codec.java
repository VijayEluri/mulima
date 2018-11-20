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
