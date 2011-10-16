package x.org.mulima.internal.audio.file;

import java.io.File;

import x.org.mulima.api.audio.file.DiscFile;
import x.org.mulima.api.meta.Disc;

public class DefaultDiscFile extends AbstractAudioFile implements DiscFile {
	private final int discNum;
	private final Disc disc;
	
	public DefaultDiscFile(File file, Disc disc) {
		super(file);
		this.disc = disc;
		this.discNum = -1;
	}
	
	public DefaultDiscFile(File file, int discNum) {
		super(file);
		if (discNum < 0) {
			throw new IllegalArgumentException("Disc cannot be less than zero.");
		}
		this.discNum = discNum;
		this.disc = null;
	}
	
	@Override
	public int getDiscNum() {
		if (disc == null) {
			return discNum;	
		} else {
			return disc.getNum();
		}
	}

	@Override
	public Disc getMeta() {
		return disc;
	}
}