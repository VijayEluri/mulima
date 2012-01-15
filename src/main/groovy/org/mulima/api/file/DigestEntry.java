package org.mulima.api.file;

import java.io.File;

public interface DigestEntry {
	File getFile();
	long getModified();
	long getSize();
	String getDigest();
	boolean lazyEquals(Object o);
}
