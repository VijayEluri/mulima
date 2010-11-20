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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.andrewoberstar.library.meta.CueSheet;
import com.andrewoberstar.library.meta.Metadata;

public class CodecService {
	private CodecConfig config;
	private ExecutorService executor;
	
	public CodecService() {
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	public CodecService(CodecConfig config) {
		this();
		this.config = config;
	}
	
	public Future<AudioFile> submitEncode(AudioFile source, AudioFile dest) {
		Codec codec = config.getCodec(dest);
		return executor.submit(codec.encodeLater(source, dest));
	}
	
	public Future<AudioFile> submitDecode(AudioFile source, AudioFile dest) {
		Codec codec = config.getCodec(source);
		return executor.submit(codec.decodeLater(source, dest));
	}
	
	public Future<List<AudioFile>> submitSplit(AudioFile source, CueSheet cue, File destDir) {
		AudioFileUtil util = config.getAudioFileUtil();
		return executor.submit(util.splitLater(source, cue, destDir));
	}
	
	public Future<AudioFile> submitJoin(List<AudioFile> sources, AudioFile dest) {
		AudioFileUtil util = config.getAudioFileUtil();
		return executor.submit(util.joinLater(sources, dest));
	}
	
	public Future<Metadata> submitReadMeta(AudioFile file) {
		MetadataUtil util = config.getMetadataUtil(file);
		return executor.submit(util.readLater(file));
	}
	
	public Future<AudioFile> submitWriteMeta(AudioFile file, Metadata meta) {
		MetadataUtil util = config.getMetadataUtil(file);
		return executor.submit(util.writeLater(file, meta));
	}
	
	public void shutdown() {
		executor.shutdown();
	}
	
	public List<Runnable> shutdownNow() {
		return executor.shutdownNow();
	}
}
