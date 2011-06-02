/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mulima.audio.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Callable;

import org.mulima.api.audio.AudioFile;
import org.mulima.api.audio.CodecConfig;
import org.mulima.api.audio.CodecResult;
import org.mulima.api.audio.SplitterResult;
import org.mulima.api.audio.TaggerResult;
import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.meta.CueSheet;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Track;
import org.mulima.exception.ConversionFailureException;
import org.mulima.exception.ProcessExecutionException;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to do the conversion of one reference album to
 * its destination albums.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class AudioConversion implements Callable<List<LibraryAlbum>> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final LibraryAlbum refAlbum;
	private final List<LibraryAlbum> destAlbums;
	private final CodecConfig codecConfig;
	
	/**
	 * Constructs a conversion for the given albums.
	 * @param codecConfig the codec configurations to use 
	 * @param refAlbum the reference album for this conversion
	 * @param destAlbums the destination albums for this conversion
	 */
	public AudioConversion(CodecConfig codecConfig, LibraryAlbum refAlbum, List<LibraryAlbum> destAlbums) {
		this.codecConfig = codecConfig;
		this.refAlbum = refAlbum;
		this.destAlbums = destAlbums;
	}
	
	/**
	 * Execute the conversion.
	 */
	@Override
	public List<LibraryAlbum> call() throws ConversionFailureException {
		try {
			List<AudioFile> files = tag(encode(split(decode(refAlbum))));
			if (files == null) {
				throw new ConversionFailureException("Failed to convert " 
					+ refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
			} else {
				return destAlbums;
			}
		} catch (ProcessExecutionException e) {
			throw new ConversionFailureException(e);
		} catch (IOException e) {
			throw new ConversionFailureException(e);
		}
	}
	
	/**
	 * Decodes the specified album.
	 * @param refAlbum the album to decode
	 * @return the decoded audio files.
	 * @throws ProcessExecutionException if the decode fails to execute
	 * @throws IOException if there is an problem creating the files
	 */
	private List<AudioFile> decode(LibraryAlbum refAlbum) throws ProcessExecutionException, IOException {
		if (refAlbum == null) {
			return null;
		}
		
		logger.info("Decoding " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
		List<AudioFile> tempFiles = new ArrayList<AudioFile>();
		for (AudioFile file : refAlbum.getAudioFiles()) {
			CodecResult result = codecConfig.getCodec(file).decode(file, AudioFile.createTempFile(file));
			if (result.getExitVal() == 0) {
				tempFiles.add(result.getDest());
			} else {
				logger.error("Failed decoding "
					+ FileUtil.getSafeCanonicalPath(result.getSource()));
				logger.error("Command: " + result.getCommand());
				logger.error("Stdout: " + result.getOutput());
				logger.error("StdErr: " + result.getError());
				return null;
			}
		}
		
		logger.debug("Decoded " + tempFiles.size() + " file(s) for " + refAlbum.getDir().getName());
		return tempFiles;
	}
	
	/**
	 * Splits the specified files.
	 * @param decodedFiles the files to decode
	 * @return the split files
	 * @throws ProcessExecutionException if the split fails to execute
	 */
	private List<AudioFile> split(List<AudioFile> decodedFiles) throws ProcessExecutionException {
		if (decodedFiles == null) {
			return null;
		}
		
		logger.info("Splitting " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
		File tempFolder;
		try {
			tempFolder = FileUtil.createTempDir("library", "wav");
		} catch (IOException e) {
			logger.error("Failed to create temp folder for: " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
			return null;
		}
		
		List<AudioFile> tempFiles = new ArrayList<AudioFile>();
		for (AudioFile file : decodedFiles) {
			CueSheet cue = refAlbum.getCue(file.getDiscNum());
			SplitterResult result = codecConfig.getSplitter().split(file, cue, tempFolder);
			if (result.getExitVal() == 0) {
				tempFiles.addAll(result.getDest());
			} else {
				logger.error("Failed splitting "
					+ FileUtil.getSafeCanonicalPath(result.getSource()));
				logger.error("Command: " + result.getCommand());
				logger.error("Stdout: " + result.getOutput());
				logger.error("StdErr: " + result.getError());
				return null;
			}
		}
		
		logger.debug("Split " + tempFiles.size() + " file(s) for " + refAlbum.getDir().getName());
		return tempFiles;
	}
	
	/**
	 * Encodes the specified files.
	 * @param splitFiles the files to encode
	 * @return the encoded files
	 * @throws ProcessExecutionException if the encode fails to execute
	 */
	private List<AudioFile> encode(List<AudioFile> splitFiles) throws ProcessExecutionException {
		if (splitFiles == null) {
			return null;
		}
		
		logger.info("Encoding " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM)); 
		List<AudioFile> tempFiles = new ArrayList<AudioFile>();
		for (LibraryAlbum destAlbum : destAlbums) {
			for (AudioFile tempFile : tempFiles) {
				AudioFile destFile = new AudioFile(destAlbum.getDir(), FileUtil.getBaseName(tempFile)
						+ "." + destAlbum.getLib().getType().getExtension());
				CodecResult result = codecConfig.getCodec(destFile).encode(tempFile, destFile);
				if (result.getExitVal() == 0) {
					tempFiles.add(result.getDest());
					destAlbum.getAudioFiles().add(result.getDest());
				} else {
					logger.error("Failed encoding "
						+ FileUtil.getSafeCanonicalPath(result.getSource()));
					logger.error("Command: " + result.getCommand());
					logger.error("Stdout: " + result.getOutput());
					logger.error("StdErr: " + result.getError());
					return null;
				}
			}
		}
		return tempFiles;
	}
	
	/**
	 * Tag the specified files.
	 * @param encodedFiles the files to tag
	 * @return the tagged files
	 * @throws ProcessExecutionException if the tag fails to execute
	 */
	private List<AudioFile> tag(List<AudioFile> encodedFiles) throws ProcessExecutionException {
		if (encodedFiles == null) {
			return null;
		}
		
		logger.info("Tagging " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
		SortedSet<Track> tracks = refAlbum.getAlbum().flatten();
		for (LibraryAlbum destAlbum : destAlbums) {
			for (AudioFile file : destAlbum.getAudioFiles()) {
				Track track = findTrack(tracks, file.getDiscNum(), file.getTrackNum());
				TaggerResult result = codecConfig.getTagger(file).write(file, track);
				if (result.getExitVal() != 0) {
					logger.error("Failed tagging "
						+ FileUtil.getSafeCanonicalPath(result.getFile()));
					logger.error("Command: " + result.getCommand());
					logger.error("Stdout: " + result.getOutput());
					logger.error("StdErr: " + result.getError());
					return null;
				}
			}
		}
		return encodedFiles;
	}
	
	/**
	 * Finds a track based on the disc and track number.
	 * @param tracks the list of tracks
	 * @param discNum the disc number
	 * @param trackNum the track number
	 * @return the track that matches
	 */
	private Track findTrack(SortedSet<Track> tracks, int discNum, int trackNum) {
		for (Track track : tracks) {
			int tempDisc = Integer.parseInt(track.getFirst(GenericTag.DISC_NUMBER));
			int tempTrack = Integer.parseInt(track.getFirst(GenericTag.TRACK_NUMBER));
			if (tempDisc == discNum && tempTrack == trackNum) {
				return track;				
			}
		}
		logger.warn("Track (Disc: " + discNum + ", Track: " + trackNum + ") not found for: "
			+ refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
		return null;
	}	
}
