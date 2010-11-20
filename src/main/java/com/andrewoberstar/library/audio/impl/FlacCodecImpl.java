package com.andrewoberstar.library.audio.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.audio.Codec;

public class FlacCodecImpl implements Codec {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String path = "flac";
	private String opts = "";
	private int compressionLevel = 5;
	
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param opts the opts to set
	 */
	public void setOpts(String opts) {
		this.opts = opts;
	}

	/**
	 * @param compressionLevel the compressionLevel to set
	 */
	public void setCompressionLevel(int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	@Override
	public AudioFile encode(AudioFile source, AudioFile dest) throws Exception {
		return encodeLater(source, dest).call();
	}

	@Override
	public AudioFile decode(AudioFile source, AudioFile dest) throws Exception {
		return decodeLater(source, dest).call();
	}

	@Override
	public Callable<AudioFile> encodeLater(AudioFile source, AudioFile dest) {
		String sourcePath;
		String destPath;
		try {
			sourcePath = source.getFile().getCanonicalPath();
			destPath = dest.getFile().getCanonicalPath();
		} catch (IOException e) {
			logger.error("Problem getting path.", e);
			return null;
		}
		
		List<String> command = new ArrayList<String>();
		command.add(path);
		command.add("-f");
		if (!"".equals(opts))
			command.add(opts);
		command.add("-" + compressionLevel);
		command.add("-o");
		command.add("\"" + destPath + "\"");
		command.add("\"" + sourcePath + "\"");
		 
		return new CodecCaller(command, source, dest);
	}

	@Override
	public Callable<AudioFile> decodeLater(AudioFile source, AudioFile dest) {
		String sourcePath;
		String destPath;
		try {
			sourcePath = source.getFile().getCanonicalPath();
			destPath = dest.getFile().getCanonicalPath();
		} catch (IOException e) {
			logger.error("Problem getting path.", e);
			return null;
		}
		
		List<String> command = new ArrayList<String>();
		command.add(path);
		command.add("-f");
		if (!"".equals(opts))
			command.add(opts);
		command.add("-d");
		command.add("-o");
		command.add("\"" + destPath + "\"");
		command.add("\"" + sourcePath + "\"");
		 
		return new CodecCaller(command, source, dest);
	}
}
