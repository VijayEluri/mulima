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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.audio.tool.Splitter;
import org.mulima.api.audio.tool.SplitterResult;
import org.mulima.api.file.CachedDir;
import org.mulima.api.file.FileService;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.file.audio.DiscFile;
import org.mulima.api.file.audio.TrackFile;
import org.mulima.api.meta.Track;
import org.mulima.api.proc.ProcessResult;
import org.mulima.internal.proc.ProcessCaller;
import org.mulima.internal.service.MulimaPropertiesSupport;
import org.mulima.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Support for shntool splitting based on a cue sheet.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Component
public class ShnToolSplitter extends MulimaPropertiesSupport implements Splitter {
	private static final Pattern SPLIT_FILE_REGEX = Pattern.compile("^split-track(\\d+)\\.");
	//private final Logger logger = LoggerFactory.getLogger(getClass());
	private FileService fileService = null;
	private String path = "shntool";
	private String opts = "";
	private boolean overwrite = false;
	
	@Override
	protected List<String> getScope() {
		return Arrays.asList("splitter", "path");
	}
	
	/**
	 * Sets the file service to use.
	 * @param fileService the file service
	 */
	@Autowired
	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}
	
	public String getPath() {
		return getProperties().getProperty("path", path);
	}
	
	/**
	 * Sets the path to the executable.
	 * @param path the exe path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getOpts() {
		return getProperties().getProperty("opts", opts);
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
		command.add(getPath());
		command.add("split");
		if (!"".equals(getOpts())) {
			command.add(getOpts());
		}
		command.add("-O");
		command.add(overwrite ? "always" : "never");
		command.add("-d");
		command.add("\"" + destPath + "\"");
		command.add("\"" + sourcePath + "\"");
		
		boolean track0 = true;
		StringWriter input = new StringWriter();
		PrintWriter writer = new PrintWriter(input);
		for (Track track : source.getMeta().getTracks()) {
			String time = track.getStartPoint().getTime();
			if ("00:00:00".equals(time)) {
				track0 = false;
			}
			writer.println(time.replaceAll(":([^:\\.]+)$", ".$1"));
		}
		writer.close();
		
		ProcessResult procResult = new ProcessCaller("split of " + FileUtil.getSafeCanonicalPath(source), command, input.toString()).call();
		
		int offset = track0 ? -1 : 0;
		for (File file : destDir.listFiles()) {
			Matcher matcher = SPLIT_FILE_REGEX.matcher(file.getName());
			if (matcher.find()) {
				int num = Integer.parseInt(matcher.group(1)) + offset;
				if (num == 0) {
					file.delete();
				} else {
					file.renameTo(new File(destDir, String.format("D%02dT%02d.%s", source.getDiscNum(), num, AudioFormat.WAVE.getExtension())));
				}
			}
		}
		
		CachedDir<AudioFile> dest = fileService.createCachedDir(AudioFile.class, destDir);
		return new SplitterResult(source, dest.getValues(TrackFile.class), procResult);
	}
	
	
}
