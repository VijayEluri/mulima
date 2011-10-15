package x.org.mulima.internal.file;

import java.io.File;

import x.org.mulima.api.file.Digest;
import x.org.mulima.api.file.DigestService;
import x.org.mulima.api.library.LibraryAlbum;

public class DefaultDigestService implements DigestService {
	private DigestDao dao = new DigestDao();
	
	@Override
	public Digest build(LibraryAlbum libAlbum) {
		return new DigestBuilder(libAlbum).build();
	}

	@Override
	public Digest read(LibraryAlbum libAlbum) {
		return dao.parse(new File(libAlbum.getDir(), Digest.FILE_NAME));
	}

	@Override
	public Digest readSource(LibraryAlbum libAlbum) {
		return dao.parse(new File(libAlbum.getDir(), Digest.SOURCE_FILE_NAME));
	}

	@Override
	public void write(LibraryAlbum libAlbum) {
		dao.compose(new File(libAlbum.getDir(), Digest.FILE_NAME), build(libAlbum));
		if (libAlbum.getSource() != null) {
			dao.compose(new File(libAlbum.getDir(), Digest.SOURCE_FILE_NAME), build(libAlbum.getSource()));
		}
	}
}
