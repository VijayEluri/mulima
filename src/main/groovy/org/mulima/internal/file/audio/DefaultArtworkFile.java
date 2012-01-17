package org.mulima.internal.file.audio;

import java.io.File;

import org.mulima.api.file.audio.ArtworkFile;
import org.mulima.api.file.audio.ArtworkFormat;

public class DefaultArtworkFile implements ArtworkFile {
	private final File file;
	private final ArtworkFormat format;
	
	public DefaultArtworkFile(File file) {
		this.file = file;
		this.format = ArtworkFormat.valueOf(file);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArtworkFormat getFormat() {
		return format;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getFile() {
		return file;
	}
}
