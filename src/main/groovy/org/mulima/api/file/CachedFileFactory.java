package org.mulima.api.file;

import java.io.File;

public interface CachedFileFactory {
	<T> CachedFile<T> valueOf(File file, Class<T> type);
}
