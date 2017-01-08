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

import org.mulima.api.file.audio.DiscFile;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.Metadata;

/**
 * Default implementation of a disc file.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DefaultDiscFile extends AbstractAudioFile implements DiscFile {
  private int discNum;
  private Disc disc;

  /**
   * Constructs a disc file from the parameters.
   *
   * @param file the file
   * @param disc the metadata
   */
  public DefaultDiscFile(File file, Disc disc) {
    super(file);
    if (disc == null) {
      throw new NullPointerException("Disc cannot be null.");
    }
    this.disc = disc;
    this.discNum = -1;
  }

  /**
   * Constructs a disc file from the parameters.
   *
   * @param file the file
   * @param discNum the disc number
   */
  public DefaultDiscFile(File file, int discNum) {
    super(file);
    if (discNum < 0) {
      throw new IllegalArgumentException("Disc cannot be less than zero.");
    }
    this.discNum = discNum;
    this.disc = null;
  }

  /** {@inheritDoc} */
  @Override
  public int getDiscNum() {
    if (disc == null) {
      return discNum;
    } else {
      return disc.getNum();
    }
  }

  /** {@inheritDoc} */
  @Override
  public Disc getMeta() {
    return disc;
  }

  /** {@inheritDoc} */
  @Override
  public void setMeta(Metadata meta) {
    if (meta instanceof Disc) {
      this.disc = (Disc) meta;
    } else {
      throw new IllegalArgumentException("DiscFiles only accept Disc metadata.");
    }
  }
}
