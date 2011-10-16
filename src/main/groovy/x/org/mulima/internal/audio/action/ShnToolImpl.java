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
package x.org.mulima.internal.audio.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import x.org.mulima.api.audio.action.Splitter;
import x.org.mulima.api.audio.action.SplitterResult;
import x.org.mulima.api.audio.file.AudioFile;
import x.org.mulima.api.audio.file.DiscFile;
import x.org.mulima.api.audio.file.TrackFile;
import x.org.mulima.api.file.CachedDir;
import x.org.mulima.api.meta.CueSheet;
import x.org.mulima.api.proc.ProcessResult;
import x.org.mulima.internal.audio.file.DefaultAudioFileFactory;
import x.org.mulima.internal.file.DefaultCachedDir;
import x.org.mulima.internal.proc.ProcessCaller;
import x.org.mulima.util.FileUtil;

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
		command.add("-f");
		command.add("\"" + cuePath + "\"");
		command.add("\"" + sourcePath + "\"");
		
		ProcessResult procResult = new ProcessCaller("split of " + FileUtil.getSafeCanonicalPath(source), command).call();
		
		CachedDir<TrackFile> dest = new DefaultCachedDir<TrackFile>(new DefaultAudioFileFactory(), destDir);
		return new SplitterResult(source, dest.getValues(), procResult);
	}
}
