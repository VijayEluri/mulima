package org.mulima.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mulima.api.library.LibraryAlbum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDigestService implements DigestService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Map<UUID, Digest> cache = new HashMap<UUID, Digest>();
	private DigestDao dao = new DigestDao();
	
	@Override
	public Digest buildDigest(LibraryAlbum libAlbum) throws IOException {
		synchronized(cache) { 
			if (!cache.containsKey(libAlbum.getId())) {
				try {
					cache.put(libAlbum.getId(), new DigestBuilder(libAlbum).build());
				} catch (IOException e) {
					logger.error("Problem generating digest for {}", libAlbum, e);
					throw e;
				}
			}
		}
		return cache.get(libAlbum.getId());
	}

	@Override
	public void readDigests(LibraryAlbum libAlbum) throws IOException {
		try {
			libAlbum.setDigest(dao.read(libAlbum));
		} catch (FileNotFoundException e) {
			logger.debug("No digest found in {}", libAlbum.getDir(), e);
		}
		try {
			libAlbum.setSourceDigest(dao.readSource(libAlbum));
		} catch (FileNotFoundException e) {
			logger.debug("No source digest found in {}", libAlbum.getDir(), e);
		}
	}

	@Override
	public void writeDigests(LibraryAlbum libAlbum) throws IOException {
		dao.write(libAlbum);
		dao.writeSource(libAlbum);
	}
}
