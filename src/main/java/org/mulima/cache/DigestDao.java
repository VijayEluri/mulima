package org.mulima.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.mulima.api.library.LibraryAlbum;

public class DigestDao {
	public static final String DIGEST_NAME = ".digest";
	public static final String SOURCE_DIGEST_NAME = ".source.digest";
	private static final String ID_KEY = "id";
	
	public Digest read(LibraryAlbum album) throws IOException {
		return read(album.getDir(), new File(album.getDir(), DIGEST_NAME));
	}
	
	public Digest readSource(LibraryAlbum album) throws IOException {
		return read(album.getDir(), new File(album.getDir(), SOURCE_DIGEST_NAME));
	}
	
	public Digest read(File dir, File file) throws IOException {
		Properties props = new Properties();
		InputStream is = new FileInputStream(file);
		props.load(is);
		is.close();
		
		UUID id = null;
		Map<File, String> fileToDigest = new HashMap<File, String>();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			if (ID_KEY.equals(entry.getKey())) {
				id = UUID.fromString((String) entry.getValue());
			}
			File key = new File(dir, (String) entry.getKey());
			fileToDigest.put(key, (String) entry.getValue());
		}
		return new Digest(id, fileToDigest);
	}
	
	public void write(LibraryAlbum album) throws IOException {
		write(new File(album.getDir(), DIGEST_NAME), album.getDigest());
	}
	
	public void writeSource(LibraryAlbum album) throws IOException {
		write(new File(album.getDir(), SOURCE_DIGEST_NAME), album.getSourceDigest());
	}
	
	public void write(File file, Digest digest) throws IOException {
		if (digest == null) {
			throw new NullPointerException("Digest cannot be null.");
		} else if (digest.getId() == null) {
			throw new NullPointerException("Digest ID cannot be null.");
		}
		
		Properties props = new Properties();
		props.setProperty(ID_KEY, digest.getId().toString());
		for (Map.Entry<File, String> entry : digest.getMap().entrySet()) {
			props.setProperty(entry.getKey().getName(), entry.getValue());
		}
		PrintWriter writer = new PrintWriter(file);
		props.store(writer, null);
		writer.close();
	}
}
