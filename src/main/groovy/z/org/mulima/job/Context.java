package z.org.mulima.job;

import z.org.mulima.cache.DefaultDigestService;
import z.org.mulima.cache.DigestService;

public class Context {
	private final TempDir tempDir;
	private final DigestService digestService;
	
	public Context() {
		this.tempDir = new TempDir();
		this.digestService = new DefaultDigestService();
	}
	
	public Context(Context context) {
		this.tempDir = context.getTempDir().newChild();
		this.digestService = context.getDigestService();
	}
	
	public TempDir getTempDir() {
		return tempDir;
	}
	
	public DigestService getDigestService() {
		return digestService;
	}
	
	public Context newChild() {
		return new Context(this);
	}
}
