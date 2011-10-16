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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.org.mulima.api.audio.action.Tagger;
import x.org.mulima.api.audio.action.TaggerResult;
import x.org.mulima.api.audio.file.AudioFile;
import x.org.mulima.api.meta.GenericTag;
import x.org.mulima.api.meta.Track;
import x.org.mulima.api.proc.ProcessResult;
import x.org.mulima.internal.meta.DefaultTrack;
import x.org.mulima.internal.meta.ITunesTag;
import x.org.mulima.internal.proc.ProcessCaller;
import x.org.mulima.util.FileUtil;

/**
 * Support for Nero AAC read/write tag operations.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class NeroAacDaoImpl implements Tagger {
	private static final Pattern REGEX = Pattern.compile("([A-Za-z]+) = (.+)");
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String path = "neroAacTag";
	
	/**
	 * Sets the path to the executable.
	 * @param path exe path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaggerResult write(AudioFile file) {
		String filePath = FileUtil.getSafeCanonicalPath(file);
		
		List<String> command = new ArrayList<String>();
		command.add(path);
		command.add("\"" + filePath + "\"");
		for (GenericTag generic : file.getMeta().getMap().keySet()) {
			ITunesTag tag = ITunesTag.valueOf(generic);
			if (tag != null) {
				for (String value : file.getMeta().getAll(tag)) {
					command.add("-meta-user:" + tag.toString() + "=" + value);
				}
			}
		}
		
		logger.info("Starting: setting tags on " + filePath);
		ProcessResult result = new ProcessCaller(command).call();
		return new TaggerResult(file, result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaggerResult read(AudioFile file) {
		String filePath = FileUtil.getSafeCanonicalPath(file);
		
		List<String> command = new ArrayList<String>();
		command.add(path);
		command.add("\"" + filePath + "\"");
		command.add("-list-meta");
		
		logger.info("Starting: reading tags from " + filePath);
		ProcessResult result = new ProcessCaller(command).call();
		
		Track track = new DefaultTrack();
		for (String line : result.getOutput().split("\n")) {
			Matcher matcher = REGEX.matcher(line.trim());
			if (matcher.matches()) {
				String name = matcher.group(1).toLowerCase();
				
				ITunesTag tag = ITunesTag.valueOf(name);
				if (tag != null) {
					track.add(tag, matcher.group(2));
				}
			}
		}
		
		return new TaggerResult(file, result);
	}
}
