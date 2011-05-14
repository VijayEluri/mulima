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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.mulima.audio.AudioFile;
import org.mulima.audio.CodecResult;
import org.mulima.audio.SplitterResult;
import org.mulima.audio.TaggerResult;
import org.mulima.exception.ConversionFailureException;
import org.mulima.library.LibraryAlbum;
import org.mulima.meta.CueSheet;
import org.mulima.meta.GenericTag;
import org.mulima.meta.Track;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class AudioConversion implements Callable<List<LibraryAlbum>> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final LibraryAlbum refAlbum;
	private final List<LibraryAlbum> destAlbums;
	private final CodecService service;
	
	public AudioConversion(CodecService service, LibraryAlbum refAlbum, List<LibraryAlbum> destAlbums) {
		this.service = service;
		this.refAlbum = refAlbum;
		this.destAlbums = destAlbums;
	}
	
	@Override
	public List<LibraryAlbum> call() throws ConversionFailureException {
		List<AudioFile> files = tag(encode(split(decode(refAlbum))));
		if (files == null) {
			throw new ConversionFailureException("Failed to convert " 
				+ refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
		} else {
			return destAlbums;
		}
	}
	
	private List<AudioFile> decode(LibraryAlbum refAlbum) {
		if (refAlbum == null) {
			return null;
		}
		
		logger.info("Decoding " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
		List<Future<CodecResult>> futures = new ArrayList<Future<CodecResult>>();
		for (AudioFile file : refAlbum.getAudioFiles()) {
			try {
				futures.add(service.submitDecode(file, AudioFile.createTempFile(file)));
			} catch (IOException e) {
				logger.error("Failed to create temp file for: " + file);
				return null;
			}
		}
		
		List<AudioFile> tempFiles = new ArrayList<AudioFile>();
		for (Future<CodecResult> future : futures) {
			try {
				CodecResult result = future.get();
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
			} catch (InterruptedException e) {
				logger.error("Failed decoding.", e);
				return null;
			} catch (ExecutionException e) {
				logger.error("Failed decoding.", e);
				return null;
			}
		}
		logger.debug("Decoded " + tempFiles.size() + " file(s) for " + refAlbum.getDir().getName());
		return tempFiles;
	}
	
	private List<AudioFile> split(List<AudioFile> decodedFiles) {
		if (decodedFiles == null) {
			return null;
		}
		
		logger.info("Splitting " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
		File tempFolder;
		try {
			tempFolder = FileUtil.createTempDir("library", "wav");
		} catch (IOException e1) {
			logger.error("Failed to create temp folder for: " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
			return null;
		}
		
		List<Future<SplitterResult>> futures = new ArrayList<Future<SplitterResult>>();
		for (AudioFile file : decodedFiles) {
			//CueSheet cue = refAlbum.getAlbum().getCues().get(file.getDiscNum() - 1);
			CueSheet cue = refAlbum.getCues().get(file.getDiscNum() - 1);
			futures.add(service.submitSplit(file, cue, tempFolder));
		}
		
		List<AudioFile> tempFiles = new ArrayList<AudioFile>();
		for (Future<SplitterResult> future : futures) {
			try {
				SplitterResult result = future.get();
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
			} catch (InterruptedException e) {
				logger.error("Failed splitting.", e);
				return null;
			} catch (ExecutionException e) {
				logger.error("Failed splitting.", e);
				return null;
			}
		}
		logger.debug("Split " + tempFiles.size() + " file(s) for " + refAlbum.getDir().getName());
		return tempFiles;
	}
	
	private List<AudioFile> encode(List<AudioFile> splitFiles) {
		if (splitFiles == null) {
			return null;
		}
		
		logger.info("Encoding " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM)); 
		List<AudioFile> tempFiles = new ArrayList<AudioFile>();
		for (LibraryAlbum destAlbum : destAlbums) {
			List<Future<CodecResult>> encFutures = new ArrayList<Future<CodecResult>>();
			for (AudioFile tempFile : tempFiles) {
				AudioFile destFile = new AudioFile(destAlbum.getDir(), FileUtil.getBaseName(tempFile)
					+ "." + destAlbum.getLib().getType().getExtension());
				encFutures.add(service.submitEncode(tempFile, destFile));
			}
			
			for (Future<CodecResult> future : encFutures) {
				try {
					CodecResult result = future.get();
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
				} catch (InterruptedException e) {
					logger.error("Failed encoding.", e);
					return null;
				} catch (ExecutionException e) {
					logger.error("Failed encoding.", e);
					return null;
				}
			}
		}
		return tempFiles;
	}
	
	private List<AudioFile> tag(List<AudioFile> encodedFiles) {
		if (encodedFiles == null) {
			return null;
		}
		
		logger.info("Tagging " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
		List<Track> tracks = refAlbum.getAlbum().flat();
		
		for (LibraryAlbum destAlbum : destAlbums) {
			List<Future<TaggerResult>> tagFutures = new ArrayList<Future<TaggerResult>>();
			for (AudioFile file : destAlbum.getAudioFiles()) {
				Track track = findTrack(tracks, file.getDiscNum(), file.getTrackNum());
				tagFutures.add(service.submitWriteMeta(file, track));
			}
			
			for (Future<TaggerResult> future : tagFutures) {
				try {
					TaggerResult result = future.get();
					if (result.getExitVal() != 0) {
						logger.error("Failed tagging "
							+ FileUtil.getSafeCanonicalPath(result.getFile()));
						logger.error("Command: " + result.getCommand());
						logger.error("Stdout: " + result.getOutput());
						logger.error("StdErr: " + result.getError());
						return null;
					}
				} catch (InterruptedException e) {
					logger.error("Failed tagging.", e);
					return null;
				} catch (ExecutionException e) {
					logger.error("Failed tagging.", e);
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
	private Track findTrack(List<Track> tracks, int discNum, int trackNum) {
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
