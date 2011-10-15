package x.org.mulima.api.file;

import java.io.File;

import x.org.mulima.internal.file.DefaultCachedFile;

public class CachedFileFactory<T> {
	private final FileParser<T> parser;
	
	public CachedFileFactory(FileParser<T> parser) {
		this.parser = parser;
	}
	
	public CachedFile<T> create(File file) {
		return new DefaultCachedFile<T>(parser, file);
	}
}
