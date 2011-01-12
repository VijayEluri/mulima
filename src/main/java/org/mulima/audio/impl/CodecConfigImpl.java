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

import java.util.HashMap;
import java.util.Map;

import org.mulima.audio.AudioFile;
import org.mulima.audio.AudioFileType;
import org.mulima.audio.Codec;
import org.mulima.audio.CodecConfig;
import org.mulima.audio.Joiner;
import org.mulima.audio.Splitter;
import org.mulima.meta.Track;
import org.mulima.meta.dao.MetadataFileDao;

public class CodecConfigImpl implements CodecConfig {
	private Map<AudioFileType, Codec> codecs = new HashMap<AudioFileType, Codec>();
	private Map<AudioFileType, MetadataFileDao<Track>> metaDaos = new HashMap<AudioFileType, MetadataFileDao<Track>>();
	private Splitter splitter = null;
	private Joiner joiner = null;
	
	/**
	 * @param codecs the codecs to set
	 */
	public void setCodecs(Map<AudioFileType, Codec> codecs) {
		this.codecs = codecs;
	}

	/**
	 * @param metaDaos the metaDaos to set
	 */
	public void setMetaDaos(Map<AudioFileType, MetadataFileDao<Track>> metaDaos) {
		this.metaDaos = metaDaos;
	}

	/**
	 * @param splitter the splitter to set
	 */
	public void setSplitter(Splitter splitter) {
		this.splitter = splitter;
	}
	
	/**
	 * @param joiner the joiner to set
	 */
	public void setJoiner(Joiner joiner) {
		this.joiner = joiner;
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
	public MetadataFileDao<Track> getMetadataFileDao(AudioFileType type) {
		return metaDaos.get(type);
	}

	@Override
	public MetadataFileDao<Track> getMetadataFileDao(AudioFile file) {
		return getMetadataFileDao(file.getType());
	}

	@Override
	public Splitter getSplitter() {
		return splitter;
	}

	@Override
	public Joiner getJoiner() {
		return joiner;
	}
}
