package z.org.mulima.api.library.impl;

import z.org.mulima.api.file.TrackFile;
import z.org.mulima.api.library.LibraryAlbum;

public class TrackLibraryAlbum extends AbstractLibraryAlbum<TrackFile> implements LibraryAlbum<TrackFile> {
	public TrackFile getAudioFile(int discNum, int trackNum) {
		for (TrackFile file : getAudioFiles()) {
			if (file.getDiscNum() == discNum && file.getTrackNum() == trackNum) {
				return file;
			}
		}
		return null;
	}
}
