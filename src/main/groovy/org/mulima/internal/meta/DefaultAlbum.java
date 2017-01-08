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
package org.mulima.internal.meta;

import java.util.SortedSet;
import java.util.TreeSet;

import org.mulima.api.meta.Album;
import org.mulima.api.meta.Disc;

/**
 * Default implementation of an album.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class DefaultAlbum extends AbstractMetadata implements Album {
  private final SortedSet<Disc> discs = new TreeSet<Disc>();

  public DefaultAlbum() {
    super(null);
  }

  /** {@inheritDoc} */
  @Override
  public SortedSet<Disc> getDiscs() {
    return discs;
  }

  /** {@inheritDoc} */
  @Override
  public Disc getDisc(int num) {
    for (Disc disc : discs) {
      if (disc.getNum() == num) {
        return disc;
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void tidy() {
    tidy(getDiscs());
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof DefaultAlbum) {
      DefaultAlbum that = (DefaultAlbum) obj;
      return this.getMap().equals(that.getMap()) && this.getDiscs().equals(that.getDiscs());
    } else {
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int result = 23;
    result = result * 31 + getMap().hashCode();
    result = result * 31 + getDiscs().hashCode();
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[tags:");
    builder.append(getMap());
    builder.append(", discs:");
    builder.append(getDiscs());
    builder.append("]");
    return builder.toString();
  }
}
