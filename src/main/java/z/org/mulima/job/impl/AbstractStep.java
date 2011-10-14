package z.org.mulima.job.impl;

import java.io.File;
import java.util.UUID;

import z.org.mulima.job.Context;

public abstract class AbstractStep {
	private final File tempDir;
	
	public AbstractStep(Context context) {
		this.tempDir = new File(context.getTempDir(), UUID.randomUUID().toString());
	}
	
	public File getTempDir() {
		return tempDir;
	}
}
