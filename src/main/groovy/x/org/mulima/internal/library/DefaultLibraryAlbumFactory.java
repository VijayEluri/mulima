package x.org.mulima.internal.library;

import java.io.File;
import java.util.UUID;

import org.mulima.util.StringUtil;

import x.org.mulima.api.MulimaService;
import x.org.mulima.api.library.Library;
import x.org.mulima.api.library.LibraryAlbum;
import x.org.mulima.api.library.LibraryAlbumFactory;
import x.org.mulima.api.meta.Disc;
import x.org.mulima.api.meta.GenericTag;

public class DefaultLibraryAlbumFactory implements LibraryAlbumFactory {
	private final MulimaService service;
	
	public DefaultLibraryAlbumFactory(MulimaService service) {
		this.service = service;
	}
	
	@Override
	public LibraryAlbum create(Library lib, LibraryAlbum source) {
		String album = null;
		if (source.getAlbum().isSet(GenericTag.ALBUM)) {
			album = source.getAlbum().getFlat(GenericTag.ALBUM);
		} else {
			for (Disc disc : source.getAlbum().getDiscs()) {
				if (disc.isSet(GenericTag.ALBUM)) {
					if (album == null) {
						album = disc.getFlat(GenericTag.ALBUM);
					} else {
						album = StringUtil.commonString(album, disc.getFlat(GenericTag.ALBUM));
					}
				}
			}
		}
		String relPath = StringUtil.makeSafe(source.getAlbum().getFlat(GenericTag.ARTIST)).trim()
			+ File.separator + StringUtil.makeSafe(album).trim();
		UUID id = UUID.randomUUID();
		File dir = new File(lib.getRootDir(), relPath);
		return new DefaultLibraryAlbum(service, id, dir, lib, source);
	}
	
	@Override
	public LibraryAlbum parse(File dir) {
		Library lib = service.getLibFor(dir);
		return new DefaultLibraryAlbum(service, null, dir, lib, null);
	}
}
