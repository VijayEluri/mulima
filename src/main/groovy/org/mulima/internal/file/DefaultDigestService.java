package org.mulima.internal.file;

import java.io.File;

import org.mulima.api.file.Digest;
import org.mulima.api.file.DigestService;
import org.mulima.api.library.LibraryAlbum;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a digest service.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Service
public class DefaultDigestService implements DigestService {
	private DigestDao dao = new DigestDao();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Digest create(LibraryAlbum libAlbum) {
		return new DigestBuilder(libAlbum).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Digest read(LibraryAlbum libAlbum) {
		return dao.parse(new File(libAlbum.getDir(), Digest.FILE_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Digest readSource(LibraryAlbum libAlbum) {
		return dao.parse(new File(libAlbum.getDir(), Digest.SOURCE_FILE_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(LibraryAlbum libAlbum, LibraryAlbum sourceAlbum) {
		dao.compose(new File(libAlbum.getDir(), Digest.FILE_NAME), create(libAlbum));
		if (sourceAlbum != null) {
			dao.compose(new File(libAlbum.getDir(), Digest.SOURCE_FILE_NAME), create(sourceAlbum));
		}
	}
}
