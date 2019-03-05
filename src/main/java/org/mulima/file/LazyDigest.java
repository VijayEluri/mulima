package org.mulima.file;

import java.util.Set;
import java.util.UUID;

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
