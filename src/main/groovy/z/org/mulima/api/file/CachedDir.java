package z.org.mulima.api.file;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;


public final class CachedDir<T> {
	private final File dir;
	private final FileFilter filter;
	private final CachedFileFactory<T> factory;
	private long lastRefreshed = 0;
	private Set<CachedFile<T>> cached;
	
	public CachedDir(File dir, FileFilter filter, CachedFileFactory<T> factory) {
		this.dir = dir;
		this.filter = filter;
		this.factory = factory;
	}
	
	public File getDir() {
		return dir;
	}
	
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
	
	public Set<File> getFiles() {
		return new ViewSet<CachedFile<T>, File>(getCachedFiles(), new CachedFileFileViewer<T>());
	}
	
	public Set<T> getValues() {
		return new ViewSet<CachedFile<T>, T>(getCachedFiles(), new CachedFileValueViewer<T>());
	}
	
	private static class CachedFileFileViewer<T> implements Viewer<CachedFile<T>, File> {
		@Override
		public File view(CachedFile<T> object) {
			return object.getFile();
		}
	}
	
	private static class CachedFileValueViewer<T> implements Viewer<CachedFile<T>, T> {
		@Override
		public T view(CachedFile<T> object) {
			return object.getValue();
		}
	}
}
