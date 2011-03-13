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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mulima.audio.AudioFile;
import org.mulima.audio.CodecResult;
import org.mulima.audio.SplitterResult;
import org.mulima.audio.TaggerResult;
import org.mulima.exception.ProcessFailureException;
import org.mulima.library.LibraryAlbum;
import org.mulima.meta.CueSheet;
import org.mulima.meta.GenericTag;
import org.mulima.meta.Track;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service that allows the conversion of one library album
 * to its destination libraries.
 */
public class AudioConversionService {
	private ExecutorService executor;
	private CodecService codecSrv;
	
	/**
	 * Constructs a service without a <code>CodecService</code>.
	 */
	public AudioConversionService() {
		this(null);
	}
	
	/**
	 * Constructs a service with the specified <code>CodecService</code>
	 * @param codecSrv codec service to use for underlying conversions
	 */
	public AudioConversionService(CodecService codecSrv) {
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		this.codecSrv = codecSrv;
	}
	
	/**
	 * Sets the codec service to use for underlying conversions.
	 * @param codecSrv the codec service
	 */
	public void setCodecSrv(CodecService codecSrv) {
		this.codecSrv = codecSrv;
	}
	
	/**
	 * Submits a conversion of the specified library album to the
	 * destinations.  Uses the underlying codec service.
	 * @param refAlbum the library album to convert
	 * @param destAlbums the destinations for the converted album
	 * @return a future list of library albums
	 */
	public Future<List<LibraryAlbum>> submitConvert(LibraryAlbum refAlbum, List<LibraryAlbum> destAlbums) {
		return executor.submit(new AudioConversion(refAlbum, destAlbums));
	}
	
	/**
	 * Initiates an orderly shutdown in which previously submitted tasks
	 * are executed, but no new tasks will be accepted. Invocation has 
	 * no additional effect if already shut down.
	 */
	public void shutdown() {
		executor.shutdown();
		codecSrv.shutdown();
	}
	
	/**
	 * Attempts to stop all actively executing tasks, halts the processing of 
	 * waiting tasks, and returns a list of the tasks that were awaiting execution. 
	 * There are no guarantees beyond best-effort attempts to stop processing 
	 * actively executing tasks. For example, typical implementations will 
	 * cancel via Thread.interrupt, so any task that fails to respond to interrupts 
	 * may never terminate.
	 * @return a list of all tasks that weren't executed
	 */
	public List<Runnable> shutdownNow() {
		List<Runnable> left = executor.shutdownNow();
		left.addAll(codecSrv.shutdownNow());
		return left;
	}
	
	private class AudioConversion implements Callable<List<LibraryAlbum>> {
		private final Logger logger = LoggerFactory.getLogger(getClass());
		private final LibraryAlbum refAlbum;
		private final List<LibraryAlbum> destAlbums;
		
		public AudioConversion(LibraryAlbum refAlbum, List<LibraryAlbum> destAlbums) {
			this.refAlbum = refAlbum;
			this.destAlbums = destAlbums;
		}
		
		@Override
		public List<LibraryAlbum> call() throws Exception {
			logger.info("Starting conversion of " + refAlbum.getDir().getName());
			List<AudioFile> decoded = decode();
			List<AudioFile> tempFiles = split(decoded);
			
			for (LibraryAlbum destAlbum : destAlbums) {
				encode(destAlbum, tempFiles);
			}
			
			for (LibraryAlbum destAlbum : destAlbums) {
				tag(destAlbum);
			}
			
			logger.info("Completed conversion of " + refAlbum.getDir().getName());
			return destAlbums;
		}
		
		private List<AudioFile> decode() throws Exception {
			logger.info("Decoding " + refAlbum.getDir().getName());
			List<Future<CodecResult>> futures = new ArrayList<Future<CodecResult>>();
			for (AudioFile file : refAlbum.getAudioFiles()) {
				futures.add(codecSrv.submitDecode(file, AudioFile.createTempFile(file)));
			}
			
			List<AudioFile> tempFiles = new ArrayList<AudioFile>();
			for (Future<CodecResult> future : futures) {
				CodecResult result = future.get();
				if (result.getExitVal() != 0) {
					Exception e = new ProcessFailureException("Failed decoding " + result.getSource().getCanonicalPath());
					logger.error("Decoding failed", e);
					logger.error("Command: " + result.getCommand());
					logger.error("Stdout: " + result.getOutput());
					logger.error("StdErr: " + result.getError());
					throw e;
				} else {
					tempFiles.add(result.getDest());
				}
			}
			logger.debug("Decoded " + tempFiles.size() + " file(s) for " + refAlbum.getDir().getName());
			return tempFiles;
		}
		
		private List<AudioFile> split(List<AudioFile> decoded) throws Exception {
			logger.info("Splitting " + refAlbum.getDir().getName());
			File tempFolder = FileUtil.createTempDir("library", "wav");
			
			List<Future<SplitterResult>> futures = new ArrayList<Future<SplitterResult>>();
			for (AudioFile file : decoded) {
				//CueSheet cue = refAlbum.getAlbum().getCues().get(file.getDiscNum() - 1);
				CueSheet cue = refAlbum.getCues().get(file.getDiscNum() - 1);
				futures.add(codecSrv.submitSplit(file, cue, tempFolder));
			}
			
			List<AudioFile> tempFiles = new ArrayList<AudioFile>();
			for (Future<SplitterResult> future : futures) {
				SplitterResult result = future.get();
				if (result.getExitVal() != 0) {
					Exception e = new ProcessFailureException("Failed splitting " + result.getSource().getCanonicalPath());
					logger.error("Splitting failed", e);
					logger.error("Command: " + result.getCommand());
					logger.error("Stdout: " + result.getOutput());
					logger.error("StdErr: " + result.getError());
					throw e;
				} else {
					tempFiles.addAll(result.getDest());
				}
			}
			logger.debug("Split " + tempFiles.size() + " file(s) for " + refAlbum.getDir().getName());
			return tempFiles;
		}
		
		private void encode(LibraryAlbum libAlbum, List<AudioFile> tempFiles) throws Exception {
			logger.info("Encoding " + refAlbum.getDir().getName()); 
			
			List<Future<CodecResult>> encFutures = new ArrayList<Future<CodecResult>>();
			for (AudioFile tempFile : tempFiles) {
				AudioFile destFile = new AudioFile(libAlbum.getDir(), FileUtil.getBaseName(tempFile) + "." + libAlbum.getLib().getType().getExtension());
				encFutures.add(codecSrv.submitEncode(tempFile, destFile));
			}
			
			for (Future<CodecResult> future : encFutures) {
				CodecResult result = future.get();
				if (result.getExitVal() != 0) {
					Exception e = new ProcessFailureException("Failed encoding " + result.getSource().getCanonicalPath());
					logger.error("Encoding failed", e);
					logger.error("Command: " + result.getCommand());
					logger.error("Stdout: " + result.getOutput());
					logger.error("StdErr: " + result.getError());
					throw e;
				} else {
					libAlbum.getAudioFiles().add(result.getDest());
				}
			}
		}
		
		private void tag(LibraryAlbum libAlbum) throws Exception {
			logger.info("Tagging " + refAlbum.getDir().getName());
			
			List<Track> tracks = libAlbum.getAlbum().flat();
			List<Future<TaggerResult>> tagFutures = new ArrayList<Future<TaggerResult>>();
			for (AudioFile file : libAlbum.getAudioFiles()) {
				Track track = findTrack(tracks, file.getDiscNum(), file.getTrackNum());
				tagFutures.add(codecSrv.submitWriteMeta(file, track));
			}
			
			for (Future<TaggerResult> future : tagFutures) {
				TaggerResult result = future.get();
				if (result.getExitVal() != 0) {
					Exception e = new ProcessFailureException("Failed tagging " + result.getFile().getCanonicalPath());
					logger.error("Tagging failed", e);
					logger.error("Command: " + result.getCommand());
					logger.error("Stdout: " + result.getOutput());
					logger.error("StdErr: " + result.getError());
					throw e;
				}
			}
		}
		
		private Track findTrack(List<Track> tracks, int discNum, int trackNum) {
			for (Track track : tracks) {
				int tempDisc = Integer.parseInt(track.getFirst(GenericTag.DISC_NUMBER));
				int tempTrack = Integer.parseInt(track.getFirst(GenericTag.TRACK_NUMBER));
				if (tempDisc == discNum && tempTrack == trackNum)
					return track;				
			}
			logger.warn("Track (Disc: " + discNum + ", Track: " + trackNum + ") not found for: " + refAlbum.getAlbum().getFlat(GenericTag.ALBUM));
			return null;
		}
	}
}
