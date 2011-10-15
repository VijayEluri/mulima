package z.org.mulima.job.impl;

import java.util.HashSet;
import java.util.Set;

import org.mulima.api.library.LibraryAlbum;

import z.org.mulima.job.Context;
import z.org.mulima.job.Job;

public class AlbumConversionJob implements Job {
	private final Context context;
	private final LibraryAlbum refAlbum;
	private final Set<LibraryAlbum> destAlbums;
	private Set<LibraryAlbum> outdatedAlbums;
	
	public AlbumConversionJob(Context parentContext, LibraryAlbum refAlbum, Set<LibraryAlbum> destAlbums) {
		this.context = parentContext.newChild();
		this.refAlbum = refAlbum;
		this.destAlbums = destAlbums;
	}

	@Override
	public boolean execute() {
		if (isUpToDate()) {
			//log skipping message
			return true;
		}
		DecodeStep decode = new DecodeStep(context, codec, refAlbum.getAudioFiles());
		if (!decode.execute()) {
			//log failure message
			return false;
		}
		SplitStep split = new SplitStep(context, splitter, decode.getDestinationFiles());
		if (!split.execute()) {
			//log failure message
			return false;
		}
		
		for (LibraryAlbum destAlbum : getOutdatedAlbums()) {
			EncodeStep encode = new EncodeStep(context, codec, split.getDestinationFiles(), destAlbum.getDir());
			if (!encode.execute()) {
				//log failure message
				return false;
			}
			TagStep tag = new TagStep(context, tagger, encode.getDestinationFiles());
			if (!tag.execute()) {
				//log failure message
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean isUpToDate() {
		return getOutdatedAlbums().size() > 0;
	}
	
	private Set<LibraryAlbum> getOutdatedAlbums() {
		if (outdatedAlbums == null) {
			//TODO: implement
			return new HashSet<LibraryAlbum>();
		} else {
			return outdatedAlbums;
		}
	}
}
