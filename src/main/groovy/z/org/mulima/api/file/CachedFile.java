package z.org.mulima.api.file;

import java.io.File;

public final class CachedFile<T> {
	private final FileParser<T> parser;
	private final File file;
	private long lastRefreshed = 0;
	private T cached;
	
	public CachedFile(FileParser<T> parser, File file) {
		this.parser = parser;
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	
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
