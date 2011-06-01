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
package org.mulima.api.audio.impl;

import java.util.HashMap;
import java.util.Map;

import org.mulima.api.audio.AudioFile;
import org.mulima.api.audio.AudioFileType;
import org.mulima.api.audio.Codec;
import org.mulima.api.audio.CodecConfig;
import org.mulima.api.audio.Joiner;
import org.mulima.api.audio.Splitter;
import org.mulima.api.audio.Tagger;

/**
 * Implementation of a codec configuration object.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @see Codec, Tagger, Splitter, Joiner
 * @since 0.1.0
 */
public class CodecConfigImpl implements CodecConfig {
	private Map<AudioFileType, Codec> codecs = new HashMap<AudioFileType, Codec>();
	private Map<AudioFileType, Tagger> taggers = new HashMap<AudioFileType, Tagger>();
	private Splitter splitter = null;
	private Joiner joiner = null;
	
	/**
	 * Sets the codecs to be supported by this config.
	 * @param codecs the codecs
	 */
	public void setCodecs(Map<AudioFileType, Codec> codecs) {
		this.codecs = codecs;
	}

	/**
	 * Sets the taggers to be supported by this config.
	 * @param taggers the taggers
	 */
	public void setTaggers(Map<AudioFileType, Tagger> taggers) {
		this.taggers = taggers;
	}

	/**
	 * Sets the splitter supported by this config.
	 * @param splitter the splitter
	 */
	public void setSplitter(Splitter splitter) {
		this.splitter = splitter;
	}
	
	/**
	 * Sets the joiner supported by this config.
	 * @param joiner the joiner
	 */
	public void setJoiner(Joiner joiner) {
		this.joiner = joiner;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Codec getCodec(AudioFileType type) {
		return codecs.get(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Codec getCodec(AudioFile file) {
		return getCodec(file.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tagger getTagger(AudioFileType type) {
		return taggers.get(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tagger getTagger(AudioFile file) {
		return getTagger(file.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Splitter getSplitter() {
		return splitter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Joiner getJoiner() {
		return joiner;
	}
}
