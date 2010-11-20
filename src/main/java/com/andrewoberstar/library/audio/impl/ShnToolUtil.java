package com.andrewoberstar.library.audio.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.audio.AudioFileUtil;
import com.andrewoberstar.library.exception.CodecDestExistsException;
import com.andrewoberstar.library.exception.CodecFailureException;
import com.andrewoberstar.library.exception.CodecSourceMissingException;
import com.andrewoberstar.library.meta.CueSheet;
import com.andrewoberstar.library.util.ProcessFuture;

public class ShnToolUtil implements AudioFileUtil {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String path = "shntool";
	private String opts = "";
	private boolean overwrite = false;
	
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
	 * @param overwrite the overwrite to set
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	@Override
	public List<AudioFile> split(AudioFile image, CueSheet cue, File destDir) throws Exception {
		return splitLater(image, cue, destDir).call();
	}
	
	@Override
	public AudioFile join(List<AudioFile> files, AudioFile dest) throws Exception {
		return joinLater(files, dest).call();
	}

	@Override
	public Callable<List<AudioFile>> splitLater(AudioFile source, CueSheet cue, File destDir) {		
		String sourcePath;
		String destPath;
		String cuePath;
		try {
			sourcePath = source.getFile().getCanonicalPath();
			destPath = destDir.getCanonicalPath();
			cuePath = cue.getFile().getCanonicalPath();
		} catch (IOException e) {
			logger.error("Problem getting path.", e);
			return null;
		}
		
		List<String> command = new ArrayList<String>();
		command.add(path);
		if (!"".equals(opts))
			command.add(opts);
		command.add("-O");
		command.add(overwrite ? "always" : "never");
		command.add("-d");
		command.add("\"" + destPath + "\"");
		command.add("-t");
		command.add("\"D" + cue.getNum() + "T%n\"");
		command.add("-f");
		command.add(cuePath);
		command.add("\"" + sourcePath + "\"");
		
		return new Splitter(command, source, destDir);
	}

	@Override
	public Callable<AudioFile> joinLater(List<AudioFile> files, AudioFile dest) {
		// TODO implement this
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	private static class Splitter implements Callable<List<AudioFile>> {
		private final Logger logger = LoggerFactory.getLogger(getClass());
		private final List<String> command;
		private final AudioFile source;
		private final File destDir;
		
		public Splitter(List<String> command, AudioFile source, File destDir) {
			this.command = command;
			this.source = source;
			this.destDir = destDir;
		}
		
		@Override
		public List<AudioFile> call() throws Exception {
			if (!source.getFile().exists()) {
				throw new CodecSourceMissingException("Source file does not exist: " + source.getFile().getName());
			} else if (!destDir.exists()) {
				throw new CodecDestExistsException("Must set overwrite to true: " + destDir.getName());
			} else {
				logger.info("Executing command: " + command);
				ProcessFuture proc = new ProcessFuture(new ProcessBuilder(command).start());
				int exit = proc.get();
				if (exit > 0) {
					logger.error("Command failed.");
					logger.error("Stdout: " + proc.getOutput());
					logger.error("Stderr: " + proc.getError());
					throw new CodecFailureException("Coding failed for source (" + source.getFile().getName() + ") to destDir (" + destDir.getName() + ").");
				} else {
					List<AudioFile> files = new ArrayList<AudioFile>();
					for (File file : destDir.listFiles()) {
						Matcher matcher = Pattern.compile("^D([0-9]+)T([0-9]+)").matcher(file.getName());
						if (matcher.matches()) {
							files.add(new AudioFile(file));
						}
					}
					return files;
				}
			}
		}
	}
}
