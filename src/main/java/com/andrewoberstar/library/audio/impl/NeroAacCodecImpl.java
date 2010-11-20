/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library.audio.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.audio.Codec;

public class NeroAacCodecImpl implements Codec {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String encPath = "neroAacEnc";
	private String decPath = "neroAacDec";
	private String quality = "0.5";
	private String opts = "";

	/**
	 * @param encPath the encPath to set
	 */
	public void setEncPath(String encPath) {
		this.encPath = encPath;
	}

	/**
	 * @param decPath the decPath to set
	 */
	public void setDecPath(String decPath) {
		this.decPath = decPath;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(String quality) {
		this.quality = quality;
	}

	/**
	 * @param opts the opts to set
	 */
	public void setOpts(String opts) {
		this.opts = opts;
	}

	@Override
	public AudioFile encode(AudioFile source, AudioFile dest) throws Exception {
		return encodeLater(source, dest).call();
	}

	@Override
	public AudioFile decode(AudioFile source, AudioFile dest) throws Exception {
		return decodeLater(source, dest).call();
	}

	@Override
	public Callable<AudioFile> encodeLater(AudioFile source, AudioFile dest) {
		String sourcePath;
		String destPath;
		try {
			sourcePath = source.getFile().getCanonicalPath();
			destPath = dest.getFile().getCanonicalPath();
		} catch (IOException e) {
			logger.error("Problem getting path.", e);
			return null;
		}
		
		List<String> command = new ArrayList<String>();
		command.add(encPath);
		if (!"".equals(opts))
			command.add(opts);
		command.add("-q");
		command.add(quality);
		command.add("-if");
		command.add("\"" + sourcePath + "\"");
		command.add("-of");
		command.add("\"" + destPath + "\"");
		 
		return new CodecCaller(command, source, dest);
	}

	@Override
	public Callable<AudioFile> decodeLater(AudioFile source, AudioFile dest) {
		String sourcePath;
		String destPath;
		try {
			sourcePath = source.getFile().getCanonicalPath();
			destPath = dest.getFile().getCanonicalPath();
		} catch (IOException e) {
			logger.error("Problem getting path.", e);
			return null;
		}
		
		List<String> command = new ArrayList<String>();
		command.add(decPath);
		if (!"".equals(opts))
			command.add(opts);
		command.add("-if");
		command.add("\"" + sourcePath + "\"");
		command.add("-of");
		command.add("\"" + destPath + "\"");
		 
		return new CodecCaller(command, source, dest);
	}

}
