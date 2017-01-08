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

import org.mulima.api.file.DigestEntry;

public class StoredDigestEntry extends AbstractDigestEntry implements DigestEntry {
  private final String fileName;
  private final long modified;
  private final long size;
  private final String digest;

  public StoredDigestEntry(String fileName, String notation) {
    this.fileName = fileName;
    String[] parts = notation.split(",", 3);
    if (parts.length < 3) {
      throw new IllegalArgumentException("Invalid digest entry notation: " + notation);
    }
    this.modified = Long.valueOf(parts[0]);
    this.size = Long.valueOf(parts[1]);
    this.digest = parts[2];
  }

  public StoredDigestEntry(String fileName, long modified, long size, String digest) {
    this.fileName = fileName;
    this.modified = modified;
    this.size = size;
    this.digest = digest;
  }

  @Override
  public String getFileName() {
    return fileName;
  }

  @Override
  public long getModified() {
    return modified;
  }

  @Override
  public long getSize() {
    return size;
  }

  @Override
  public String getDigest() {
    return digest;
  }
}
