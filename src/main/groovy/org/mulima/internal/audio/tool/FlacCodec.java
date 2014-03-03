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

package org.mulima.internal.audio.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mulima.api.audio.tool.Codec;
import org.mulima.api.audio.tool.CodecResult;
import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.file.audio.AudioFormat;
import org.mulima.internal.proc.ProcessCaller;
import org.mulima.internal.service.MulimaPropertiesSupport;
import org.mulima.util.FileUtil;
import org.springframework.stereotype.Component;


/**
 * Support for FLAC encoding/decoding.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
@Component
public class FlacCodec extends MulimaPropertiesSupport implements Codec {
	//private final Logger logger = LoggerFactory.getLogger(getClass());
	private String path = "flac";
	private String opts = "";
	private String compressionLevel = "5";

	@Override
	protected List<String> getScope() {
		return Arrays.asList("codec", "flac");
	}

	@Override
	public AudioFormat getFormat() {
		return AudioFormat.FLAC;
	}

	/**
	 * Gets the path to the FLAC executable.
	 * @return the path to the exe
	 */
	public String getPath() {
		return getProperties().getProperty("path", path);
	}

	/**
	 * Sets the path to the FLAC executable.
	 * @param path the path to the exe
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Gets the additional options for this codec.
	 * @return the options
	 */
	public String getOpts() {
		return getProperties().getProperty("opts", opts);
	}

	/**
	 * Sets additional options for this codec.  Will be
	 * used on both encodes and decodes.
	 * @param opts the options
	 */
	public void setOpts(String opts) {
		this.opts = opts;
	}

	public String getCompressionLevel() {
		return getProperties().getProperty("compressionLevel", compressionLevel);
	}

	/**
	 * Sets the compression level for encodes.
	 * @param compressionLevel the compression level (1-8)
	 */
	public void setCompressionLevel(String compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecResult encode(AudioFile source, AudioFile dest) {
		String sourcePath = FileUtil.getSafeCanonicalPath(source);
		String destPath = FileUtil.getSafeCanonicalPath(dest);

		List<String> command = new ArrayList<String>();
		command.add(getPath());
		command.add("-f");
		if (!"".equals(getOpts())) {
			command.add(getOpts());
		}
		command.add("-" + getCompressionLevel());
		command.add("-o");
		command.add(destPath);
		command.add(sourcePath);

		ProcessCaller caller = new ProcessCaller("encoding " + sourcePath, command);
		return new CodecResult(source, dest, caller.call());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecResult decode(AudioFile source, AudioFile dest) {
		String sourcePath = FileUtil.getSafeCanonicalPath(source);
		String destPath = FileUtil.getSafeCanonicalPath(dest);

		List<String> command = new ArrayList<String>();
		command.add(getPath());
		command.add("-f");
		if (!"".equals(getOpts())) {
			command.add(getOpts());
		}
		command.add("-d");
		command.add("-o");
		command.add(destPath);
		command.add(sourcePath);

		ProcessCaller caller = new ProcessCaller("decoding " + sourcePath, command);
		return new CodecResult(source, dest, caller.call());
	}
}
