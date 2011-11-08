package org.mulima.internal.file;

import java.io.File;

import org.mulima.api.file.CachedFile;
import org.mulima.api.file.FileParser;

/**
 * Default implementation of a cached file.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 * @param <T> the type of the file's value
 */
public class DefaultCachedFile<T> implements CachedFile<T> {
	private final FileParser<T> parser;
	private final File file;
	private long lastRefreshed = 0;
	private T cached;
	
	/**
	 * Creates a cached file whose value will be retrieved
	 * using the specified parser.
	 * @param parser the parser that will work on this file.
	 * @param file the file to cache
	 */
	public DefaultCachedFile(FileParser<T> parser, File file) {
		this.parser = parser;
		this.file = file;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getFile() {
		return file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getValue() {
		long lastModified = file.lastModified();
		if (lastModified == lastRefreshed) {
			return cached;
		} else {
			T value = parser.parse(file);
			cached = value;
			lastRefreshed = lastModified;
			return cached;
		}
	}
}
