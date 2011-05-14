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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.audio.AudioFile;
import org.mulima.audio.Tagger;
import org.mulima.audio.TaggerResult;
import org.mulima.meta.GenericTag;
import org.mulima.meta.Track;
import org.mulima.meta.impl.ITunesTag;
import org.mulima.proc.ProcessCaller;
import org.mulima.proc.ProcessResult;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support for Nero AAC read/write tag operations.
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
	public TaggerResult write(AudioFile file, Track meta) throws Exception {
		return writeLater(file, meta).call();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaggerResult read(AudioFile file) throws Exception {
		return readLater(file).call();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Callable<TaggerResult> writeLater(AudioFile file, Track meta) {
		return new Writer(file, meta);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Callable<TaggerResult> readLater(AudioFile file) {
		return new Reader(file);
	}

	private class Writer implements Callable<TaggerResult> {
		private final AudioFile file;
		private final Track meta;
		
		/**
		 * Constructs a writer from parameters.
		 * @param file the file to write to
		 * @param meta the track to write
		 */
		public Writer(AudioFile file, Track meta) {
			this.file = file;
			this.meta = meta;
		}
		
		/**
		 * Executes the tag write operation.
		 */
		@Override
		public TaggerResult call() throws Exception {
			String filePath = FileUtil.getSafeCanonicalPath(file);
			
			List<String> command = new ArrayList<String>();
			command.add(path);
			command.add("\"" + filePath + "\"");
			for (GenericTag generic : meta.getMap().keySet()) {
				ITunesTag tag = ITunesTag.valueOf(generic);
				if (tag != null) {
					for (String value : meta.getAll(tag)) {
						command.add("-meta-user:" + tag.toString() + "=" + value);
					}
				}
			}
			
			logger.info("Starting: setting tags on " + filePath);
			ProcessResult result = new ProcessCaller(command).call();
			return new TaggerResult(file, meta, result);
		}
	}
	
	private class Reader implements Callable<TaggerResult> {
		private final AudioFile file;
		
		/**
		 * Constructs a reader from the parameter.
		 * @param file the file to read from
		 */
		public Reader(AudioFile file) {
			this.file = file;
		}
		
		/**
		 * Executes the tag read operation.
		 */
		@Override
		public TaggerResult call() throws Exception {
			String filePath = FileUtil.getSafeCanonicalPath(file);
			
			List<String> command = new ArrayList<String>();
			command.add(path);
			command.add("\"" + filePath + "\"");
			command.add("-list-meta");
			
			logger.info("Starting: reading tags from " + filePath);
			ProcessResult result = new ProcessCaller(command).call();
			
			Track track = new Track();
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
			
			return new TaggerResult(file, track, result);
		}
	}
}
