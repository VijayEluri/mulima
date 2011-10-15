package z.org.mulima.api.file;

import java.io.File;

public interface FileParser<T> {
	T parse(File file);
}
