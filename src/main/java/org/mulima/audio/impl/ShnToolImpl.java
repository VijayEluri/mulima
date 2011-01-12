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
package org.mulima.audio.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.audio.AudioFile;
import org.mulima.audio.Splitter;
import org.mulima.audio.SplitterResult;
import org.mulima.meta.CueSheet;
import org.mulima.util.FileUtil;
import org.mulima.util.io.ProcessCaller;
import org.mulima.util.io.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShnToolImpl implements Splitter {
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
	public SplitterResult split(AudioFile image, CueSheet cue, File destDir) throws Exception {
		return splitLater(image, cue, destDir).call();
	}

	@Override
	public Callable<SplitterResult> splitLater(AudioFile source, CueSheet cue, File destDir) {		
		String sourcePath = FileUtil.getSafeCanonicalPath(source);
		String destPath = FileUtil.getSafeCanonicalPath(destDir);
		String cuePath = FileUtil.getSafeCanonicalPath(cue.getFile());
		
		List<String> command = new ArrayList<String>();
		command.add(path);
		command.add("split");
		if (!"".equals(opts))
			command.add(opts);
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
		
		public SplitterCaller(List<String> command, AudioFile source, File destDir) {
			this.command = command;
			this.source = source;
			this.destDir = destDir;
		}
		
		@Override
		public SplitterResult call() throws Exception {
			String description = "split of " + FileUtil.getSafeCanonicalPath(source);
			logger.info("Starting: " + description);
			logger.debug("Executing command: " + command);
			ProcessResult procResult = new ProcessCaller(command).call();
			logger.info("Finished: " + description);
			
			List<AudioFile> dest = new ArrayList<AudioFile>();
			for (File file : destDir.listFiles()) {
				Matcher matcher = Pattern.compile("^D([0-9]+)T([0-9]+).*").matcher(file.getName());
				if (matcher.matches()) {
					dest.add(new AudioFile(file));
				}
			}
			
			return new SplitterResult(source, dest, procResult);
		}
	}
}
