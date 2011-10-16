package x.org.mulima.internal.library;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.UUID;

import x.org.mulima.api.MulimaService;
import x.org.mulima.api.audio.file.AudioFile;
import x.org.mulima.api.file.CachedDir;
import x.org.mulima.api.file.CachedFile;
import x.org.mulima.api.file.Digest;
import x.org.mulima.api.file.FileParser;
import x.org.mulima.api.library.Library;
import x.org.mulima.api.library.LibraryAlbum;
import x.org.mulima.api.meta.Album;
import x.org.mulima.api.meta.CueSheet;
import x.org.mulima.internal.file.DefaultCachedDir;
import x.org.mulima.internal.file.DefaultCachedFile;

public class DefaultLibraryAlbum implements LibraryAlbum {
	private final MulimaService service;
	private final UUID id;
	private final File dir;
	private final Library lib;
	private final LibraryAlbum source;
	private final CachedFile<Album> album;
	private final CachedDir<CueSheet> cues;
	private final CachedFile<Digest> digest;
	private final CachedFile<Digest> sourceDigest;
	private CachedDir<AudioFile> audioFiles;
	
	public DefaultLibraryAlbum(MulimaService service, UUID id, File dir, Library lib, LibraryAlbum source) {
		this.service = service;
		this.dir = dir;
		this.lib = lib;
		
		this.album = new DefaultCachedFile<Album>(service.getParser(Album.class), new File(dir, "album.xml"));
		this.cues = new DefaultCachedDir<CueSheet>(service.getParser(CueSheet.class), dir, new CueSheetFilter());
		
		FileParser<Digest> digestParser = service.getParser(Digest.class);
		this.digest = new DefaultCachedFile<Digest>(digestParser, new File(dir, Digest.FILE_NAME));
		this.sourceDigest = new DefaultCachedFile<Digest>(digestParser, new File(dir, Digest.SOURCE_FILE_NAME));
		
		if (id == null) {
			this.id = getDigest().getId();
		} else {
			this.id = id;
		}
		
		if (source == null) {
			this.source = service.getAlbumById(getSourceDigest().getId());
		} else {
			this.source = source;
		}
		
		this.audioFiles = new DefaultCachedDir<AudioFile>(service.getParser(AudioFile.class), dir);
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public File getDir() {
		return dir;
	}

	@Override
	public Library getLib() {
		return lib;
	}

	@Override
	public LibraryAlbum getSource() {
		return source;
	}

	@Override
	public Album getAlbum() {
		return album.getValue();
	}
	
	@Override
	public Set<AudioFile> getAudioFiles() {
		return audioFiles.getValues();
	}
	
	@Override
	public Digest getDigest() {
		return digest.getValue();
	}
	
	@Override
	public Digest getSourceDigest() {
		return sourceDigest.getValue();
	}

	@Override
	public boolean isUpToDate() {
		return isUpToDate(getDigest());
	}

	@Override
	public boolean isUpToDate(boolean checkSource) {
		return isUpToDate(getDigest(), checkSource);
	}

	@Override
	public boolean isUpToDate(Digest digest) {
		return isUpToDate(digest, true);
	}

	@Override
	public boolean isUpToDate(Digest digest, boolean checkSource) {
		if (digest == null) {
			return false;
		} else if (source != null && checkSource && !source.isUpToDate(getSourceDigest())) {
			return false;
		}
		Digest current = service.getDigestService().build(this);
		return digest == null ? false : digest.equals(current);
	}
	
	private static class CueSheetFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".cue");
		}
	}
}
