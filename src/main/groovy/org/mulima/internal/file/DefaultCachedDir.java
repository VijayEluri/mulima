package org.mulima.internal.file;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.mulima.api.file.CachedDir;
import org.mulima.api.file.CachedFile;
import org.mulima.api.file.FileService;

/**
 * Default implementation of a cached directory.
 * @author Andrew Oberstar
 * @since 0.1.0
 * @param <T> the type of the file values
 */
public class DefaultCachedDir<T> implements CachedDir<T> {
	private final FileService service;
	private final Class<T> type;
	private final File dir;
	private final FileFilter filter;
	private long lastRefreshed = 0;
	private Set<CachedFile<T>> cached;
	
	/**
	 * Creates a cached dir, that caches all files in the directory.
	 * @param service a service to create cached files
	 * @param type the type of files to cache
	 * @param dir the directory to cache
	 */
	public DefaultCachedDir(FileService service, Class<T> type, File dir) {
		this(service, type, dir, null);
	}
	
	/**
	 * Creates a cached dir, that caches all files in the directory
	 * that match the filter.
	 * @param service a service to create cached files
	 * @param type the type of files to cache
	 * @param dir the directory to cache
	 * @param filter filters the files in the directory
	 */
	public DefaultCachedDir(FileService service, Class<T> type, File dir, FileFilter filter) {
		this.service = service;
		this.type = type;
		this.dir = dir;
		this.filter = filter;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getDir() {
		return dir;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CachedFile<T>> getCachedFiles() {
		long lastModified = dir.lastModified();
		if (lastRefreshed == lastModified) {
			return cached;
		} else {
			Set<CachedFile<T>> files = new HashSet<CachedFile<T>>();
			for (File file : dir.listFiles(filter)) {
				files.add(service.createCachedFile(type, file));
			}
			this.cached = files;
			this.lastRefreshed = lastModified;
			return cached;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<File> getFiles() {
		Set<File> files = new HashSet<File>();
		for (CachedFile<T> file : getCachedFiles()) {
			files.add(file.getFile());
		}
		return files;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<T> getValues() {
		Set<T> values = new HashSet<T>();
		for (CachedFile<T> file : getCachedFiles()) {
			T value = file.getValue();
			if (value != null) {
				values.add(value);
			}
		}
		return values;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S extends T> Set<S> getValues(Class<S> type) {
		Set<S> values = new HashSet<S>();
		for (CachedFile<T> file : getCachedFiles()) {
			T value = file.getValue();
			if (type.isInstance(value)) {
				values.add(type.cast(value));
			}
		}
		return values;
	}
}
