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
package org.mulima.api.audio.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.audio.AudioFile;
import org.mulima.api.audio.Splitter;
import org.mulima.api.audio.SplitterResult;
import org.mulima.api.meta.CueSheet;
import org.mulima.exception.ProcessExecutionException;
import org.mulima.proc.ProcessCaller;
import org.mulima.proc.ProcessResult;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support for shntool splitting based on a cue sheet.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class ShnToolImpl implements Splitter {
	//private final Logger logger = LoggerFactory.getLogger(getClass());
	private String path = "shntool";
	private String opts = "";
	private boolean overwrite = false;
	
	/**
	 * Sets the path to the executable.
	 * @param path the exe path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Sets the additional options to set.
	 * @param opts the options
	 */
	public void setOpts(String opts) {
		this.opts = opts;
	}

	/**
	 * Sets whether or not to overwrite existing files.
	 * @param overwrite overwrite value
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SplitterResult split(AudioFile image, CueSheet cue, File destDir) throws ProcessExecutionException {
		return prepSplit(image, cue, destDir).call();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Callable<SplitterResult> splitLater(AudioFile source, CueSheet cue, File destDir) {		
		return prepSplit(source, cue, destDir);
	}
	
	private SplitterCaller prepSplit(AudioFile source, CueSheet cue, File destDir) {
		String sourcePath = FileUtil.getSafeCanonicalPath(source);
		String destPath = FileUtil.getSafeCanonicalPath(destDir);
		String cuePath = FileUtil.getSafeCanonicalPath(cue.getFile());
		
		List<String> command = new ArrayList<String>();
		command.add(path);
		command.add("split");
		if (!"".equals(opts)) {
			command.add(opts);
		}
		command.add("-O");
		command.add(overwrite ? "always" : "never");
		command.add("-d");
		command.add("\"" + destPath + "\"");
		command.add("-t");
		command.add("\"D" + cue.getNum() + "T%n\"");
		command.add("-f");
		command.add("\"" + cuePath + "\"");
		command.add("\"" + sourcePath + "\"");
		
		return new SplitterCaller(command, source, destDir);
	}
	
	private static class SplitterCaller implements Callable<SplitterResult> {
		private final Logger logger = LoggerFactory.getLogger(getClass());
		private final List<String> command;
		private final AudioFile source;
		private final File destDir;
		
		/**
		 * Constructs a splitter caller from parameters.
		 * @param command the command to execute
		 * @param source the source file
		 * @param destDir the destination directory
		 */
		public SplitterCaller(List<String> command, AudioFile source, File destDir) {
			this.command = command;
			this.source = source;
			this.destDir = destDir;
		}
		
		/**
		 * Executes the split operation.
		 */
		@Override
		public SplitterResult call() throws ProcessExecutionException {
			String description = "split of " + FileUtil.getSafeCanonicalPath(source);
			logger.info("Starting: " + description);
			logger.debug("Executing command: " + command);
			ProcessResult procResult = new ProcessCaller(command).call();
			logger.info("Finished: " + description);
			
			List<AudioFile> dest = new ArrayList<AudioFile>();
			for (File file : destDir.listFiles()) {
				Matcher matcher = Pattern.compile("^D([0-9]+)T([0-9]+).*").matcher(file.getName());
				if (matcher.matches()) {
					AudioFile audioFile = new AudioFile(file);
					audioFile.setDiscNum(Integer.valueOf(matcher.group(1)));
					audioFile.setTrackNum(Integer.valueOf(matcher.group(2)));
					dest.add(audioFile);
				}
			}
			
			return new SplitterResult(source, dest, procResult);
		}
	}
}
