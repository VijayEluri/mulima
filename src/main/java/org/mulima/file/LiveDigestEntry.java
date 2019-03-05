package org.mulima.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LiveDigestEntry extends AbstractDigestEntry implements DigestEntry {
  private static final Logger logger = LogManager.getLogger(LiveDigestEntry.class);
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
