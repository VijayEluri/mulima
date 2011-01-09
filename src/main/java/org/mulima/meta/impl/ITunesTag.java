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

public enum ITunesTag implements Tag {
	album(GenericTag.ALBUM),
	sortalbum(GenericTag.ALBUM_SORT),
	label(GenericTag.ORGANIZATION),

	artist(GenericTag.ARTIST),
	sortartist(GenericTag.ARTIST_SORT),
	composer(GenericTag.COMPOSER),

	disc(GenericTag.DISC_NUMBER),
	track(GenericTag.TRACK_NUMBER),
	title(GenericTag.TITLE),
	sorttitle(GenericTag.TITLE_SORT),
	genre(GenericTag.GENRE),
	year(GenericTag.DATE),
	lyrics(GenericTag.LYRICS),
	comment(GenericTag.DESCRIPTION);
	
	private final GenericTag tag;
	
	private ITunesTag(GenericTag tag) {
		this.tag = tag;
	}

	@Override
	public GenericTag getGeneric() {
		return tag;
	}
	
	@Override
	public String toString() {
		return this.name().replaceAll("_", ":");
	}
	
	public static ITunesTag valueOf(GenericTag generic) {
		for (ITunesTag tag : ITunesTag.values()) {
			if (generic.equals(tag.getGeneric())) {
				return tag;
			}
		}
		return null;
	}
}
