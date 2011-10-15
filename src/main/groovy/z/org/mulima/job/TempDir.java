package z.org.mulima.job;

import java.io.File;
import java.util.UUID;

public class TempDir {
	private final File dir;
	
	public TempDir() {
		this(new File(System.getProperty("java.io.tmpdir")));
	}
	
	public TempDir(String path) {
		this(new File(path));
	}
	
	public TempDir(File tempDir) {
		this.dir = tempDir;
	}
	
	public TempDir(TempDir parent) {
		this(parent, UUID.randomUUID().toString());
	}
	
	public TempDir(TempDir parent, String name) {
		this(new File(parent.get(), name));
	}
	
	public File get() {
		return dir;
	}
	
	public TempDir newChild() {
		return new TempDir(this);
	}
}
