package x.org.mulima.internal.file;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import x.org.mulima.api.file.Digest;

public class DefaultDigest implements Digest {
	private final UUID id;
	private final Map<File, String> fileToDigest;
	
	public DefaultDigest(UUID id, Map<File, String> fileToDigest) {
		this.id = id;
		this.fileToDigest = new HashMap<File, String>(fileToDigest);
	}
	
	@Override
	public UUID getId() {
		return id;
	}
	
	@Override
	public String getDigest(File file) {
		return fileToDigest.get(file);
	}
	
	@Override
	public Map<File, String> getMap() {
		return Collections.unmodifiableMap(fileToDigest);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof DefaultDigest) {
			DefaultDigest that = (DefaultDigest) obj;
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
