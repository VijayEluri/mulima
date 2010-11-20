/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.andrewoberstar.library.AlbumFolder;
import com.andrewoberstar.library.Library;
import com.andrewoberstar.library.exception.UnknownCodecException;
import com.andrewoberstar.library.meta.CueSheet;
import com.andrewoberstar.library.meta.GenericTag;
import com.andrewoberstar.library.meta.Track;
import com.andrewoberstar.library.util.FileUtil;

public class AudioConversionService {
	private ExecutorService executor;
	private CodecService codecSrv;
	
	public AudioConversionService() {
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	public AudioConversionService(CodecService codecSrv) {
		this();
		this.codecSrv = codecSrv;
	}
	
	/**
	 * @param codecSrv the codecSrv to set
	 */
	public void setCodecSrv(CodecService codecSrv) {
		this.codecSrv = codecSrv;
	}

	public Future<List<AlbumFolder>> submitConvert(AlbumFolder refFolder, List<Library> destLibs) {
		return executor.submit(new AudioConversion(refFolder, destLibs));
	}
	
	public void shutdown() {
		executor.shutdown();
	}
	
	private class AudioConversion implements Callable<List<AlbumFolder>> {
		private final AlbumFolder refFolder;
		private final List<Library> destLibs;
		
		public AudioConversion(AlbumFolder refFolder, List<Library> destLibs) {
			this.refFolder = refFolder;
			this.destLibs = destLibs;
		}
		
		@Override
		public List<AlbumFolder> call() throws Exception {
			List<AudioFile> decoded = decode();
			List<AudioFile> tempFiles = split(decoded);
			
			List<AlbumFolder> folders = new ArrayList<AlbumFolder>();
			for (Library lib : destLibs) {
				folders.add(encode(lib, tempFiles));
			}
			
			return folders;
		}
		
		private List<AudioFile> decode() throws Exception {
			List<Future<AudioFile>> futures = new ArrayList<Future<AudioFile>>();
			for (AudioFile file : refFolder.getAudioFiles()) {
				futures.add(codecSrv.submitDecode(file, AudioFile.createTempFile(AudioFileType.WAVE)));
			}
			
			List<AudioFile> tempFiles = new ArrayList<AudioFile>();
			for (Future<AudioFile> future : futures) {
				tempFiles.add(future.get());
			}
			return tempFiles;
		}
		
		private List<AudioFile> split(List<AudioFile> decoded) throws Exception {
			File tempFolder = FileUtil.createTempDir("library", "wav");
			
			List<Future<List<AudioFile>>> futures = new ArrayList<Future<List<AudioFile>>>();
			for (AudioFile file : decoded) {
				CueSheet cue = refFolder.getCue(file);
				futures.add(codecSrv.submitSplit(file, cue, tempFolder));
			}
			
			List<AudioFile> tempFiles = new ArrayList<AudioFile>();
			for (Future<List<AudioFile>> future : futures) {
				tempFiles.addAll(future.get());
			}
			return tempFiles;
		}
		
		private AlbumFolder encode(Library lib, List<AudioFile> tempFiles) throws Exception {
			AlbumFolder folder = AlbumFolder.createAlbumFolder(lib.getRootDir(), refFolder.getAlbum()); 
			
			List<Future<AudioFile>> encFutures = new ArrayList<Future<AudioFile>>();
			for (AudioFile tempFile : tempFiles) {
				AudioFile destFile = AudioFile.createAudioFile(folder.getFolder(), tempFile, lib.getType());
				encFutures.add(codecSrv.submitEncode(tempFile, destFile));
			}
			
			List<Track> tracks = folder.getAlbum().flat();
			List<Future<AudioFile>> tagFutures = new ArrayList<Future<AudioFile>>();
			for (Future<AudioFile> future : encFutures) {
				AudioFile file = future.get();
				String name = file.getFile().getName();
				Matcher matcher = Pattern.compile("^D([0-9]+)T([0-9]+)").matcher(name);
				if (!matcher.matches())
					throw new UnknownCodecException("Invalid name.");
				Track track = findTrack(tracks, Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)));
				tagFutures.add(codecSrv.submitWriteMeta(file, track));
			}
			
			for (Future<AudioFile> future : tagFutures) {
				folder.getAudioFiles().add(future.get());
			}
			
			return folder;
		}
		
		private Track findTrack(List<Track> tracks, int discNum, int trackNum) {
			for (Track track : tracks) {
				int tempDisc = Integer.parseInt(track.getTags().getFirst(GenericTag.DISC_NUMBER));
				int tempTrack = Integer.parseInt(track.getTags().getFirst(GenericTag.TRACK_NUMBER));
				if (tempDisc == discNum && tempTrack == trackNum)
					return track;				
			}
			return null;
		}
	}
}
