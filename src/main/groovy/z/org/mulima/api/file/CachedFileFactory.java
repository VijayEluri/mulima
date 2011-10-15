package z.org.mulima.api.file;

import java.io.File;

public class CachedFileFactory<T> {
	private final FileParser<T> parser;
	
	public CachedFileFactory(FileParser<T> parser) {
		this.parser = parser;
	}
	
	public CachedFile<T> create(File file) {
		return new CachedFile<T>(parser, file);
	}
}
