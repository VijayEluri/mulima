package x.org.mulima.internal.file;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import x.org.mulima.api.file.CachedDir;
import x.org.mulima.api.file.CachedFile;
import x.org.mulima.api.file.CachedFileFactory;
import x.org.mulima.api.file.FileParser;

public class DefaultCachedDir<T> implements CachedDir<T> {
	private final File dir;
	private final FileFilter filter;
	private final CachedFileFactory<T> factory;
	private long lastRefreshed = 0;
	private Set<CachedFile<T>> cached;
	
	public DefaultCachedDir(File dir, FileFilter filter, FileParser<T> parser) {
		this.dir = dir;
		this.filter = filter;
		this.factory = new CachedFileFactory<T>(parser);
	}
	
	public File getDir() {
		return dir;
	}
	
	@Override
	public Set<CachedFile<T>> getCachedFiles() {
		long lastModified = dir.lastModified();
		if (lastRefreshed == lastModified) {
			return cached;
		} else {
			Set<CachedFile<T>> files = new HashSet<CachedFile<T>>();
			for (File file : dir.listFiles(filter)) {
				files.add(factory.create(file));
			}
			this.cached = files;
			this.lastRefreshed = lastModified;
			return cached;
		}
	}

	@Override
	public Set<File> getFiles() {
		Set<File> files = new HashSet<File>();
		for (CachedFile<T> file : getCachedFiles()) {
			files.add(file.getFile());
		}
		return files;
	}

	@Override
	public Set<T> getValues() {
		Set<T> values = new HashSet<T>();
		for (CachedFile<T> file : getCachedFiles()) {
			values.add(file.getValue());
		}
		return values;
	}
	
}
