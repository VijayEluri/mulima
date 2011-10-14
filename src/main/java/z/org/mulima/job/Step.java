package z.org.mulima.job;

import java.io.File;

public interface Step {
	void execute();
	File getTempDir();
}
