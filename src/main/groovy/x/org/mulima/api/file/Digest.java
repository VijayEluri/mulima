package x.org.mulima.api.file;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public interface Digest {
	String FILE_NAME = ".digest";
	String SOURCE_FILE_NAME = ".source.digest";
	UUID getId();	
	String getDigest(File file);
	Map<File, String> getMap();
}
