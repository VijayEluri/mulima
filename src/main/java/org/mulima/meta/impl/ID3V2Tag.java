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
package org.mulima.meta.impl;

import org.mulima.meta.GenericTag;
import org.mulima.meta.Tag;

/**
 * Enumeration representing tags used for 
 * files using ID3v2 tags.
 */
public enum ID3V2Tag implements Tag {
	TALB(GenericTag.ALBUM),
	TSOA(GenericTag.ALBUM_SORT),
	TPUB(GenericTag.ORGANIZATION),
	TDOR(GenericTag.RELEASE_DATE),

	TPE1(GenericTag.ARTIST),
	TDOP(GenericTag.ARTIST_SORT),
	TCOM(GenericTag.COMPOSER),
	TEXT(GenericTag.LYRICIST),
	TMLC(GenericTag.PERFORMER),
	TIPL_producer(GenericTag.PRODUCER),
	TIPL_engineer(GenericTag.ENGINEER),
	TPE3(GenericTag.CONDUCTOR),

	TPOS(GenericTag.DISC_NUMBER),
	TRCK(GenericTag.TRACK_NUMBER),
	TIT2(GenericTag.TITLE),
	TSOT(GenericTag.TITLE_SORT),
	TCON(GenericTag.GENRE),
	TDRC(GenericTag.DATE),
	LOCATION(GenericTag.LOCATION),
	USLT(GenericTag.LYRICS),
	COMM(GenericTag.DESCRIPTION);
	
	private final GenericTag tag;
	
	private ID3V2Tag(GenericTag tag) {
		this.tag = tag;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GenericTag getGeneric() {
		return tag;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.name().replaceAll("_", ":");
	}
	
	/**
	 * Conversion method for getting the matching <code>ID3V2Tag</code>
	 * for a given <code>GenericTag</code>.
	 * @param generic the generic tag to look for
	 * @return the ID3v2 tag that corresponds to <code>generic</code.
	 */
	public static ID3V2Tag valueOf(GenericTag generic) {
		for (ID3V2Tag tag : ID3V2Tag.values()) {
			if (generic.equals(tag.getGeneric())) {
				return tag;
			}
		}
		return null;
	}
}
