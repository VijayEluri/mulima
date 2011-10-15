package z.org.mulima.api.library.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.mulima.exception.FatalMulimaException;

import z.org.mulima.api.library.LibraryAlbum;
import z.org.mulima.api.meta.Album;
import z.org.mulima.cache.Digest;
import z.org.mulima.job.Context;

public abstract class AbstractLibraryAlbum implements LibraryAlbum {
	private final Context context;
	private final File dir;
	private final Album album;
	private final LibraryAlbum source;
	
	private long lastModified = 0;
	private UUID id;
	private Digest digest;
	private Digest sourceDigest;
	
	public AbstractLibraryAlbum(Context context, UUID id, File dir, Album album, LibraryAlbum source) {
		this.context = context;
		this.id = id;
		this.dir = dir;
		this.album = album;
		this.source = source;
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
	public Album getAlbum() {
		return album;
	}
	
	@Override
	public LibraryAlbum getSource() {
		return source;
	}
	
	@Override
	public Digest getDigest() {
		return digest;
	}
	
	@Override
	public Digest getSourceDigest() {
		return sourceDigest;
	}
	
	protected boolean refresh() {
		if (getDir().lastModified() == lastModified) {
			return false;
		}
		this.digest = context.getDigestService().readDigest(this);
		this.sourceDigest = context.getDigestService().readSourceDigest(this);
		if (digest != null) {
			if (id == null) {
				this.id = digest.getId();
			} else if (!id.equals(digest.getId())) {
				throw new FatalMulimaException("Library album ID has been modified: " + dir);
			}
		}
		return true;
	}
	
	@Override
	public boolean isUpToDate() throws IOException {
		return isUpToDate(getDigest());
	}

	@Override
	public boolean isUpToDate(boolean checkSource) throws IOException {
		return isUpToDate(getDigest(), checkSource);
	}

	@Override
	public boolean isUpToDate(Digest digest) throws IOException {
		return isUpToDate(digest, true);
	}

	@Override
	public boolean isUpToDate(Digest digest, boolean checkSource) throws IOException {
		if (digest == null) {
			return false;
		} else if (source != null && checkSource && !source.isUpToDate(getSourceDigest())) {
			return false;
		}
		
		Digest current = context.getDigestService().buildDigest(this);
		return digest == null ? false : digest.equals(current);
	}
}
