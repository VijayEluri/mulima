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
package org.mulima.internal.audio.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mulima.api.audio.tool.Splitter;
import org.mulima.api.audio.tool.SplitterResult;
import org.mulima.api.file.CachedDir;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.DiscFile;
import org.mulima.api.file.audio.TrackFile;
import org.mulima.api.proc.ProcessResult;
import org.mulima.internal.file.DefaultCachedDir;
import org.mulima.internal.file.audio.DefaultAudioFileFactory;
import org.mulima.internal.proc.ProcessCaller;
import org.mulima.util.FileUtil;


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
	public SplitterResult split(DiscFile source, File destDir) {
		String sourcePath = FileUtil.getSafeCanonicalPath(source);
		String destPath = FileUtil.getSafeCanonicalPath(destDir);
		
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
		command.add("\"D" + source.getDiscNum() + "T%n\"");
		//command.add("-f");
		//command.add("\"" + cuePath + "\"");
		command.add("\"" + sourcePath + "\"");
		
		ProcessResult procResult = new ProcessCaller("split of " + FileUtil.getSafeCanonicalPath(source), command).call();
		
		//CachedDir<AudioFile> dest = new DefaultCachedDir<AudioFile>(new DefaultAudioFileFactory(), destDir);
		//return new SplitterResult(source, dest.getValues(TrackFile.class), procResult);
		throw new UnsupportedOperationException("Finish implementing");
	}
}
