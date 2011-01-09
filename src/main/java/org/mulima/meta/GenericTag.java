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
package org.mulima.meta;

import org.mulima.util.StringUtil;

/**
 * Contains generic tags that should be common across
 * all tagging systems. Other <code>Tag</code> implementations
 * will tie their values back to values from this class.
 * @see TagSupport
 */
public enum GenericTag implements Tag {
	ALBUM,
	ALBUM_SORT,
	ORGANIZATION,
	PUBLISHER,
	PRODUCT_NUMBER,
	CATALOG_NUMBER,
	VOLUME,
	RELEASE_DATE,

	ARTIST,
	ARTIST_SORT,
	GUEST_ARTIST,
	COMPOSER,
	LYRICIST,
	PERFORMER,
	PRODUCER,
	ENGINEER,
	CONDUCTOR,
	ENSEMBLE,

	DISC_NUMBER,
	TRACK_NUMBER,
	TITLE,
	TITLE_SORT,
	PART,
	VERSION,
	OPUS,
	GENRE,
	DATE,
	LOCATION,
	LYRICS,
	DESCRIPTION,

	CDDB_ID,
	FILE;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GenericTag getGeneric() {
		return this;
	}
	
	/**
	 * Gets the camel case form of this tag.
	 * @return camel case form of tag
	 */
	public String camelCase() {
		return StringUtil.toCamelCase(this.name());
	}
	
	/**
	 * Returns the <code>GenericTag</code> that has the same camel case
	 * value as <code>arg0</code>.
	 * @param arg0 the camel case name of the constant to return
	 * @return the <code>GenericTag</code> with the specified name 
	 */
	public static GenericTag valueOfCamelCase(String arg0) {
		String name = StringUtil.fromCamelCase(arg0);
		return GenericTag.valueOf(name);
	}
}
