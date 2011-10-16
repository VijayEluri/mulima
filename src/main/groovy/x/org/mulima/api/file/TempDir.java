package x.org.mulima.api.file;

import java.io.File;
import java.util.UUID;

public class TempDir implements FileHolder {
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
		this(new File(parent.getFile(), name));
	}
	
	public File getFile() {
		return dir;
	}
	
	public TempDir newChild() {
		return new TempDir(this);
	}
}
