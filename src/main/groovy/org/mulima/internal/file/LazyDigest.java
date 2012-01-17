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
}
