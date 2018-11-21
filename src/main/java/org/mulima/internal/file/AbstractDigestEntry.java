package org.mulima.internal.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.api.file.DigestEntry;

public abstract class AbstractDigestEntry implements DigestEntry {
  private static final Logger logger = LogManager.getLogger(AbstractDigestEntry.class);

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    } else if (o instanceof AbstractDigestEntry) {
      DigestEntry that = (DigestEntry) o;
      return this.getDigest().equals(that.getDigest());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return getFileName().hashCode();
  }

  public boolean lazyEquals(Object o) {
    if (o == null) {
      return false;
    } else if (o instanceof AbstractDigestEntry) {
      DigestEntry that = (DigestEntry) o;
      long difference = Math.abs(this.getModified() - that.getModified());
      if (difference < 1000 && this.getSize() == that.getSize()) {
        return true;
      } else {
        logger.trace(
            "Digest entry size or timestamp different for {}.  Checking digest of contents.",
            getFileName());
        return this.getDigest().equals(that.getDigest());
      }
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(getModified());
    builder.append(",");
    builder.append(getSize());
    builder.append(",");
    builder.append(getDigest());
    return builder.toString();
  }
}
