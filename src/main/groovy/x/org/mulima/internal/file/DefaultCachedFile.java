package x.org.mulima.internal.file;

import java.io.File;

import x.org.mulima.api.file.CachedFile;
import x.org.mulima.api.file.FileParser;

public class DefaultCachedFile<T> implements CachedFile<T> {
	private final FileParser<T> parser;
	private final File file;
	private long lastRefreshed = 0;
	private T cached;
	
	public DefaultCachedFile(FileParser<T> parser, File file) {
		this.parser = parser;
		this.file = file;
	}
	
	@Override
	public File getFile() {
		return file;
	}

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
