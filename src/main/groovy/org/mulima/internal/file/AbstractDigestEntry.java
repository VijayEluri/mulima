package org.mulima.internal.file;

import org.mulima.api.file.DigestEntry;

public abstract class AbstractDigestEntry implements DigestEntry {
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
			if (this.getModified() == that.getModified() && this.getSize() == that.getSize()) {
				return true;
			} else {
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
