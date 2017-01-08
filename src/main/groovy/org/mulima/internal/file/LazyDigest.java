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
package org.mulima.internal.file;

import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.Digest;
import org.mulima.api.file.DigestEntry;

public class LazyDigest extends AbstractDigest implements Digest {
  public LazyDigest(UUID id, Set<? extends DigestEntry> entries) {
    super(id, entries);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    } else if (o instanceof LazyDigest) {
      Digest that = (Digest) o;
      if (!this.getId().equals(that.getId())) {
        return false;
      }
      for (DigestEntry thisEntry : this.getEntries()) {
        DigestEntry thatEntry = that.getEntry(thisEntry.getFileName());
        if (!thisEntry.lazyEquals(thatEntry)) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return getId() == null ? 0 : getId().hashCode();
  }
}
