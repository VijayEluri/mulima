package org.mulima.api.file;


public interface DigestEntry {
	String getFileName();
	long getModified();
	long getSize();
	String getDigest();
	boolean lazyEquals(Object o);
}
