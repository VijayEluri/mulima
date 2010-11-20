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

import java.util.HashMap;
import java.util.Map;

import com.andrewoberstar.library.audio.AudioFile;
import com.andrewoberstar.library.audio.AudioFileType;
import com.andrewoberstar.library.audio.AudioFileUtil;
import com.andrewoberstar.library.audio.Codec;
import com.andrewoberstar.library.audio.CodecConfig;
import com.andrewoberstar.library.audio.MetadataUtil;

public class CodecConfigImpl implements CodecConfig {
	private Map<AudioFileType, Codec> codecs = new HashMap<AudioFileType, Codec>();
	private Map<AudioFileType, MetadataUtil> metaUtils = new HashMap<AudioFileType, MetadataUtil>();
	private AudioFileUtil audioUtil = null;
	
	/**
	 * @param codecs the codecs to set
	 */
	public void setCodecs(Map<AudioFileType, Codec> codecs) {
		this.codecs = codecs;
	}

	/**
	 * @param metaUtils the metaUtils to set
	 */
	public void setMetaUtils(Map<AudioFileType, MetadataUtil> metaUtils) {
		this.metaUtils = metaUtils;
	}

	/**
	 * @param audioUtil the audioUtil to set
	 */
	public void setAudioUtil(AudioFileUtil audioUtil) {
		this.audioUtil = audioUtil;
	}

	@Override
	public Codec getCodec(AudioFileType type) {
		return codecs.get(type);
	}

	@Override
	public Codec getCodec(AudioFile file) {
		return getCodec(file.getType());
	}

	@Override
	public MetadataUtil getMetadataUtil(AudioFileType type) {
		return metaUtils.get(type);
	}

	@Override
	public MetadataUtil getMetadataUtil(AudioFile file) {
		return getMetadataUtil(file.getType());
	}

	@Override
	public AudioFileUtil getAudioFileUtil() {
		return audioUtil;
	}

}
