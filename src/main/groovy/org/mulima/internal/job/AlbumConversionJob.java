package org.mulima.internal.job;

import java.util.HashSet;
import java.util.Set;

import org.mulima.api.audio.file.AudioFile;
import org.mulima.api.audio.file.DiscFile;
import org.mulima.api.file.DigestService;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.service.MulimaService;


public class AlbumConversionJob implements Job<Boolean> {
	private final MulimaService service;
	private final LibraryAlbum refAlbum;
	private final Set<LibraryAlbum> destAlbums;
	
	public AlbumConversionJob(MulimaService service, LibraryAlbum refAlbum, Set<LibraryAlbum> destAlbums) {
		this.service = service;
		this.refAlbum = refAlbum;
		this.destAlbums = destAlbums;
	}
	
	@Override
	public Boolean call() throws Exception {
		return execute();
	}
	
	public Boolean execute() {
		Set<LibraryAlbum> outdated = getOutdatedAlbums();
		if (outdated.size() == 0) {
			//log skipping
			return true;
		}
		
		DecodeStep decode = new DecodeStep(service, refAlbum.getAudioFiles());
		if (!decode.execute()) {
			//log failure
			return false;
		}
		Set<AudioFile> tempFiles = new HashSet<AudioFile>();
		Set<DiscFile> discFiles = new HashSet<DiscFile>();
		for (AudioFile temp : decode.getOutputs()) {
			if (temp instanceof DiscFile) {
				discFiles.add((DiscFile) temp);
			} else {
				tempFiles.add(temp);
			}
		}
		SplitStep split = new SplitStep(service, discFiles);
		if (!split.execute()) {
			//log failure
			return false;
		}
		tempFiles.addAll(split.getOutputs());
		
		for (LibraryAlbum destAlbum : outdated) {
			EncodeStep encode = new EncodeStep(service, destAlbum.getLib().getFormat(), tempFiles, destAlbum.getDir());
			if (!encode.execute()) {
				//log failure
				return false;
			}
			
			TagStep tag = new TagStep(service, encode.getOutputs());
			if (!tag.execute()) {
				//log failure
				return false;
			}
		}
		
		DigestService digestService = service.getDigestService();
		for (LibraryAlbum destAlbum : outdated) {
			digestService.write(destAlbum);
		}
		
		//log success
		return true;
	}
	
	private Set<LibraryAlbum> getOutdatedAlbums() {
		Set<LibraryAlbum> tempAlbums = new HashSet<LibraryAlbum>();
		for (LibraryAlbum destAlbum : destAlbums) {
			if (!destAlbum.isUpToDate()) {
				tempAlbums.add(destAlbum);
			//} else {
			//	logger.debug("Album is up to date: {}", destAlbum.getDir());
			}
		}
		return tempAlbums;
	}
}
