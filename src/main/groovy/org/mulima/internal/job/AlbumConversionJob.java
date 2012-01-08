package org.mulima.internal.job;

import java.util.HashSet;
import java.util.Set;

import org.mulima.api.file.DigestService;
import org.mulima.api.file.TempDir;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.DiscFile;
import org.mulima.api.job.Job;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.service.MulimaService;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A job to convert albums.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class AlbumConversionJob implements Job<Boolean> {
	private final Logger LOGGER = LoggerFactory.getLogger(AlbumConversionJob.class);
	private final MulimaService service;
	private final LibraryAlbum refAlbum;
	private final Set<LibraryAlbum> destAlbums;
	
	/**
	 * Constructs a job from the parameters.
	 * @param service the service to use during job execution
	 * @param refAlbum the reference album
	 * @param destAlbums the destination albums
	 */
	public AlbumConversionJob(MulimaService service, LibraryAlbum refAlbum, Set<LibraryAlbum> destAlbums) {
		this.service = service;
		this.refAlbum = refAlbum;
		this.destAlbums = destAlbums;
	}
	
	/**
	 * Executes the job.
	 */
	@Override
	public Boolean call() throws Exception {
		return execute();
	}
	
	/**
	 * Executes the job.
	 */
	public Boolean execute() {
		LOGGER.info("Beginning conversion of: " + refAlbum.getName());
		Set<LibraryAlbum> outdated = getOutdatedAlbums();
		if (outdated.size() == 0) {
			LOGGER.info("Skipping conversion for " + refAlbum.getName() + ". No albums are out of date.");
			return true;
		}
		TempDir tempDir = service.getTempDir().newChild();
		
		DecodeStep decode = new DecodeStep(service, refAlbum.getAudioFiles(), tempDir.newChild().getFile());
		if (!decode.execute()) {
			LOGGER.error("Failed to decode: " + refAlbum.getName());
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
		SplitStep split = new SplitStep(service, discFiles, tempDir.newChild().getFile());
		if (!split.execute()) {
			LOGGER.error("Failed to split: " + refAlbum.getName());
			return false;
		}
		tempFiles.addAll(split.getOutputs());
		
		for (LibraryAlbum destAlbum : outdated) {
			destAlbum.setDir(destAlbum.getLib().determineDir(refAlbum.getAlbum()));
			destAlbum.cleanDir();
			EncodeStep encode = new EncodeStep(service, destAlbum.getLib().getFormat(), tempFiles, destAlbum.getDir());
			if (!encode.execute()) {
				LOGGER.error("Failed to encode: " + refAlbum.getName());
				return false;
			}
			
			TagStep tag = new TagStep(service, encode.getOutputs());
			if (!tag.execute()) {
				LOGGER.error("Failed to tag: " + refAlbum.getName());
				return false;
			}
			
			LOGGER.info("Starting to copy artwork to {}", destAlbum.getDir());
			FileUtil.copyAll(refAlbum.getArtwork(), destAlbum.getDir());
			LOGGER.info("Finished copying artwork to {}", destAlbum.getDir());
		}
		
		FileUtil.deleteDir(tempDir);
		
		DigestService digestService = service.getDigestService();
		for (LibraryAlbum destAlbum : outdated) {
			digestService.write(destAlbum, refAlbum);
		}
		digestService.write(refAlbum, null);
		
		LOGGER.info("Successfully converted: " + refAlbum.getName());
		return true;
	}
	
	/**
	 * Gets a set of all outdated albums.
	 * @return the set of outdated albums
	 */
	private Set<LibraryAlbum> getOutdatedAlbums() {
		Set<LibraryAlbum> tempAlbums = new HashSet<LibraryAlbum>();
		for (LibraryAlbum destAlbum : destAlbums) {
			if (!service.getLibraryService().isUpToDate(destAlbum, true)) {
				tempAlbums.add(destAlbum);
				LOGGER.debug("Album is out of date: {}", destAlbum.getDir());
			} else {
				LOGGER.debug("Album is up to date: {}", destAlbum.getDir());
			}
		}
		return tempAlbums;
	}
}
