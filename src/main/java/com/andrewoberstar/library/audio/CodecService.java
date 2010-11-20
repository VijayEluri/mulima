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
