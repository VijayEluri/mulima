package z.org.mulima.job;

import java.io.File;

public class Context {
	private static Context instance = null;
	private final File tempDir;
	
	protected Context(File tempDir) {
		this.tempDir = tempDir;
	}
	
	public File getTempDir() {
		return tempDir;
	}
	
	public static Context initInstance(File tempDir) {
		if (instance != null) {
			throw new IllegalStateException("Context already initialized.");
		}
		instance = new Context(tempDir);
		return instance;
	}
	
	public static Context getInstance() {
		if (instance == null) {
			initInstance(new File(System.getProperty("java.io.tmpdir")));
		}
		return instance;
	}
}
