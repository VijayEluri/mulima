package x.org.mulima.api.file;

import java.io.File;

public interface FileComposer<T> {
	void compose(File file, T object);
}
