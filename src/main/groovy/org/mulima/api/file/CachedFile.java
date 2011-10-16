package org.mulima.api.file;

import java.io.File;

public interface CachedFile<T> {
	File getFile();
	T getValue();
}
