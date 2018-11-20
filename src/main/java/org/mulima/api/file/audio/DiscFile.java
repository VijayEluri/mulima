package org.mulima.api.file.audio;

import org.mulima.api.meta.Disc;

/**
 * An object representing an audio file of an entire disc's worth of music. This is generally an
 * image of a CD.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface DiscFile extends AudioFile {
  /** {@inheritDoc} */
  Disc getMeta();

  /**
   * Gets the disc number.
   *
   * @return the disc number
   */
  int getDiscNum();
}
