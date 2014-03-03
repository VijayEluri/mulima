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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.api.audio.tool.Tagger;
import org.mulima.api.audio.tool.TaggerResult;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.api.meta.Track;
import org.mulima.api.proc.ProcessResult;
import org.mulima.internal.meta.DefaultTrack;
import org.mulima.internal.meta.ITunesTag;
import org.mulima.internal.proc.ProcessCaller;
import org.mulima.internal.service.MulimaPropertiesSupport;
import org.mulima.util.FileUtil;
import org.springframework.stereotype.Component;


/**
 * Support for Nero AAC read/write tag operations.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Component
public class NeroAacTagger extends MulimaPropertiesSupport implements Tagger {
	private static final Pattern REGEX = Pattern.compile("([A-Za-z]+) = (.+)");
	//private final Logger logger = LoggerFactory.getLogger(getClass());
	private String path = "neroAacTag";

	@Override
	protected List<String> getScope() {
		return Arrays.asList("tagger", "aac");
	}

	@Override
	public AudioFormat getFormat() {
		return AudioFormat.AAC;
	}

	public String getPath() {
		return getProperties().getProperty("path", path);
	}

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
		command.add(getPath());
		command.add("\"" + filePath + "\"");
		for (ITunesTag tag : ITunesTag.values()) {
			for (String value : file.getMeta().getAll(tag)) {
				String preparedValue = value.replaceAll("\"", "\\\\\"");
				command.add("-meta-user:" + tag.toString() + "=" + preparedValue + "");
			}
		}
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
		command.add(getPath());
		command.add(filePath);
		command.add("-list-meta");

		ProcessResult result = new ProcessCaller("tag of " + FileUtil.getSafeCanonicalPath(file), command).call();

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
