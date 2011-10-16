package org.mulima.api.file;

import java.io.File;
import java.util.Set;

public interface CachedDir<T> {
	Set<CachedFile<T>> getCachedFiles();
	Set<File> getFiles();
	Set<T> getValues();
	<S extends T> Set<S> getValues(Class<S> type);
}
