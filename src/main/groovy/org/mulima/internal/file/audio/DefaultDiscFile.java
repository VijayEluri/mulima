package org.mulima.internal.file.audio;

import java.io.File;

import org.mulima.api.file.audio.DiscFile;
import org.mulima.api.meta.Disc;

/**
 * Default implementation of a disc file.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultDiscFile extends AbstractAudioFile implements DiscFile {
	private final int discNum;
	private final Disc disc;
	
	/**
	 * Constructs a disc file from the parameters.
	 * @param file the file
	 * @param disc the metadata
	 */
	public DefaultDiscFile(File file, Disc disc) {
		super(file);
		if (disc == null) {
			throw new NullPointerException("Disc cannot be null.");
		}
		this.disc = disc;
		this.discNum = -1;
	}
	
	/**
	 * Constructs a disc file from the parameters.
	 * @param file the file
	 * @param discNum the disc number
	 */
	public DefaultDiscFile(File file, int discNum) {
		super(file);
		if (discNum < 0) {
			throw new IllegalArgumentException("Disc cannot be less than zero.");
		}
		this.discNum = discNum;
		this.disc = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDiscNum() {
		if (disc == null) {
			return discNum;	
		} else {
			return disc.getNum();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Disc getMeta() {
		return disc;
	}
}
