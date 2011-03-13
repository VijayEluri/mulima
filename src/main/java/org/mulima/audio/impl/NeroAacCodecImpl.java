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

package org.mulima.audio.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.mulima.audio.AudioFile;
import org.mulima.audio.Codec;
import org.mulima.audio.CodecResult;
import org.mulima.audio.util.CodecCaller;
import org.mulima.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supports Nero AAC encode/decode operations.
 */
public class NeroAacCodecImpl implements Codec {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String encPath = "neroAacEnc";
	private String decPath = "neroAacDec";
	private String quality = "0.5";
	private String opts = "";

	/**
	 * Sets the path to the encoder executable.
	 * @param encPath the encoder exe path
	 */
	public void setEncPath(String encPath) {
		this.encPath = encPath;
	}

	/**
	 * Sets the path to the decoder executable.
	 * @param decPath the decoder exe path
	 */
	public void setDecPath(String decPath) {
		this.decPath = decPath;
	}

	/**
	 * Sets the quality of the encode.
	 * @param quality the quality (0.0-1.0)
	 */
	public void setQuality(String quality) {
		this.quality = quality;
	}

	/**
	 * Sets the additional options to use.  These will
	 * be used in both encodes and decodes.
	 * @param opts the options
	 */
	public void setOpts(String opts) {
		this.opts = opts;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecResult encode(AudioFile source, AudioFile dest) throws Exception {
		return encodeLater(source, dest).call();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecResult decode(AudioFile source, AudioFile dest) throws Exception {
		return decodeLater(source, dest).call();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Callable<CodecResult> encodeLater(AudioFile source, AudioFile dest) {
		String sourcePath = FileUtil.getSafeCanonicalPath(source);
		String destPath = FileUtil.getSafeCanonicalPath(dest);
		
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
		 
		return new CodecCaller("encoding " + destPath, source, dest, command);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Callable<CodecResult> decodeLater(AudioFile source, AudioFile dest) {
		String sourcePath = FileUtil.getSafeCanonicalPath(source);
		String destPath = FileUtil.getSafeCanonicalPath(dest);
		
		List<String> command = new ArrayList<String>();
		command.add(decPath);
		if (!"".equals(opts))
			command.add(opts);
		command.add("-if");
		command.add("\"" + sourcePath + "\"");
		command.add("-of");
		command.add("\"" + destPath + "\"");
		 
		return new CodecCaller("decoding " + destPath, source, dest, command);
	}

}
