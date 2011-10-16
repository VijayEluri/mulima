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

package org.mulima.api.audio.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.mulima.api.audio.AudioFile;
import org.mulima.api.audio.Codec;
import org.mulima.api.audio.CodecResult;
import org.mulima.api.audio.util.CodecCaller;
import org.mulima.exception.ProcessExecutionException;
import org.mulima.util.FileUtil;

/**
 * Support for FLAC encoding/decoding.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class FlacCodecImpl implements Codec {
	//private final Logger logger = LoggerFactory.getLogger(getClass());
	private String path = "flac";
	private String opts = "";
	private int compressionLevel = 5;
	
	/**
	 * Sets the path to the FLAC executable.
	 * @param path the path to the exe
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Sets additional options for this codec.  Will be
	 * used on both encodes and decodes.
	 * @param opts the options
	 */
	public void setOpts(String opts) {
		this.opts = opts;
	}

	/**
	 * Sets the compression level for encodes.
	 * @param compressionLevel the compression level (1-8)
	 */
	public void setCompressionLevel(int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecResult encode(AudioFile source, AudioFile dest) throws ProcessExecutionException {
		return prepEncode(source, dest).call();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecResult decode(AudioFile source, AudioFile dest) throws ProcessExecutionException {
		return prepDecode(source, dest).call();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Callable<CodecResult> encodeLater(final AudioFile source, final AudioFile dest) {
		return prepEncode(source, dest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Callable<CodecResult> decodeLater(AudioFile source, AudioFile dest) {
		return prepDecode(source, dest);
	}
	
	private CodecCaller prepEncode(AudioFile source, AudioFile dest) { 
		String sourcePath = FileUtil.getSafeCanonicalPath(source);
		String destPath = FileUtil.getSafeCanonicalPath(dest);
		
		List<String> command = new ArrayList<String>();
		command.add(path);
		command.add("-f");
		if (!"".equals(opts)) {
			command.add(opts);
		}
		command.add("-" + compressionLevel);
		command.add("-o");
		command.add("\"" + destPath + "\"");
		command.add("\"" + sourcePath + "\"");
		
		return new CodecCaller("encoding " + sourcePath, source, dest, command);
	}
	
	private CodecCaller prepDecode(AudioFile source, AudioFile dest) {
		String sourcePath = FileUtil.getSafeCanonicalPath(source);
		String destPath = FileUtil.getSafeCanonicalPath(dest);
		
		List<String> command = new ArrayList<String>();
		command.add(path);
		command.add("-f");
		if (!"".equals(opts)) {
			command.add(opts);
		}
		command.add("-d");
		command.add("-o");
		command.add("\"" + destPath + "\"");
		command.add("\"" + sourcePath + "\"");
		 
		return new CodecCaller("decoding " + sourcePath, source, dest, command);
	}
}