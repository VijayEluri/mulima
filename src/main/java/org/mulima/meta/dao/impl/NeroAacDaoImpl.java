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
package org.mulima.meta.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mulima.meta.GenericTag;
import org.mulima.meta.Track;
import org.mulima.meta.dao.MetadataFileDao;
import org.mulima.meta.impl.ITunesTag;
import org.mulima.util.io.ProcessCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeroAacDaoImpl implements MetadataFileDao<Track> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String path = "neroAacTag";
	
	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public void write(File file, Track meta) throws Exception {
		writeLater(file, meta).call();
	}

	@Override
	public Track read(File file) throws Exception {
		return readLater(file).call();
	}

	@Override
	public Callable<Void> writeLater(File file, Track meta) {
		return new Writer(file, meta);
	}

	@Override
	public Callable<Track> readLater(File file) {
		return new Reader(file);
	}

	private class Writer implements Callable<Void> {
		private final File file;
		private final Track meta;
		
		public Writer(File file, Track meta) {
			this.file = file;
			this.meta = meta;
		}
		
		@Override
		public Void call() throws Exception {
			String filePath;
			try {
				filePath = file.getCanonicalPath();
			} catch (IOException e) {
				logger.error("Problem getting path.", e);
				return null;
			}
			
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
			
			new ProcessCaller("setting tags on " + filePath, command).call();
			return null;
		}
	}
	
	private class Reader implements Callable<Track> {
		private final File file;
		
		public Reader(File file) {
			this.file = file;
		}
		
		@Override
		public Track call() throws Exception {
			String filePath;
			try {
				filePath = file.getCanonicalPath();
			} catch (IOException e) {
				logger.error("Problem getting path.", e);
				return null;
			}
			
			List<String> command = new ArrayList<String>();
			command.add(path);
			command.add("\"" + filePath + "\"");
			command.add("-list-meta");
			
			String output = new ProcessCaller("reading tags from " + filePath, command).call();
			
			Track track = new Track();
			Pattern regex = Pattern.compile("([A-Za-z]+) = (.+)");
			for (String line : output.split("\n")) {
				Matcher matcher = regex.matcher(line.trim());
				if (matcher.matches()) {
					String name = matcher.group(1).toLowerCase();
					String value = matcher.group(2);
					
					ITunesTag tag = ITunesTag.valueOf(name);
					if (tag != null) {
						track.add(tag, value);
					}
				}
			}
			
			return track;
		}
	}
}
