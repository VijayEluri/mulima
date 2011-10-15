package z.org.mulima.cache;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Digest {
	private final UUID id;
	private final Map<File, String> fileToDigest;
	
	public Digest(UUID id, Map<File, String> fileToDigest) {
		this.id = id;
		this.fileToDigest = new HashMap<File, String>(fileToDigest);
	}
	
	public UUID getId() {
		return id;
	}
	
	public String getDigest(File file) {
		return fileToDigest.get(file);
	}
	
	public Map<File, String> getMap() {
		return Collections.unmodifiableMap(fileToDigest);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Digest) {
			Digest that = (Digest) obj;
			return id.equals(that.getId()) && fileToDigest.equals(that.fileToDigest);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return id.hashCode() + fileToDigest.hashCode();
	}
}
