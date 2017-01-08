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

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.mulima.api.file.Digest;
import org.mulima.api.file.DigestEntry;

public abstract class AbstractDigest implements Digest {
  private final UUID id;
  private Set<DigestEntry> entries;

  public AbstractDigest(UUID id, Set<? extends DigestEntry> entries) {
    this.id = id;
    this.entries = Collections.unmodifiableSet(entries);
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public String getDigest(String fileName) {
    DigestEntry entry = getEntry(fileName);
    return entry == null ? null : entry.getDigest();
  }

  @Override
  public DigestEntry getEntry(String fileName) {
    for (DigestEntry entry : entries) {
      if (entry.getFileName().equals(fileName)) {
        return entry;
      }
    }
    return null;
  }

  @Override
  public Set<DigestEntry> getEntries() {
    return entries;
  }
}
