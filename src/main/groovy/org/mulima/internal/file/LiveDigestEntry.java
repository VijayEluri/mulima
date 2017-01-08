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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.mulima.api.file.DigestEntry;
import org.mulima.exception.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveDigestEntry extends AbstractDigestEntry implements DigestEntry {
  private static final Logger logger = LoggerFactory.getLogger(LiveDigestEntry.class);
  private final File file;
  private String digest;

  public LiveDigestEntry(File file) {
    this.file = file;
  }

  @Override
  public String getFileName() {
    return file.getName();
  }

  @Override
  public long getModified() {
    return file.lastModified();
  }

  @Override
  public long getSize() {
    return file.length();
  }

  @Override
  public String getDigest() {
    if (digest == null) {
      InputStream is = null;
      try {
        is = new FileInputStream(file);
        digest = DigestUtils.shaHex(is);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      } finally {
        try {
          if (is != null) {
            is.close();
          }
        } catch (IOException e) {
          logger.warn("Problem closing stream for: {}", file.getAbsolutePath(), e);
        }
      }
    }
    return digest;
  }
}
