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
 * Enumeration representing tags used in cue sheet files. 
 * 
 * For more information see {@link http://en.wikipedia.org/wiki/Cue_sheet_(computing)}.
 */
public class CueSheetTag {
	/**
	 * Contains tags specific to the cue sheet as a whole.
	 */
	public enum Cue implements Tag {
		TITLE(GenericTag.ALBUM, "\"", "\""),
		PERFORMER(GenericTag.ARTIST, "\"", "\""),
		FILE(GenericTag.FILE, "\"", "\" WAVE"),
		REM_DATE(GenericTag.RELEASE_DATE, "", ""),
		REM_GENRE(GenericTag.GENRE, "\"", "\""),
		REM_DISCID(GenericTag.CDDB_ID, "", "");
		
		private GenericTag tag;
		private String prefix;
		private String suffix;
		
		private Cue(GenericTag tag, String prefix, String suffix) {
			this.tag = tag;
			this.prefix = prefix;
			this.suffix = suffix;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public GenericTag getGeneric() {
			return tag;
		}
		
		@Override
		public String toString() {
			return this.name().replaceAll("_", " ");
		}
		
		/**
		 * Returns the name of this tag, that includes
		 * the properly formatted value.
		 * @param value the value for this tag
		 * @return the name and value of this tag in the proper format
		 */
		public String toString(String value) {
			return this.toString() + " " + prefix + value + suffix;
		}
	}
	
	/**
	 * Contains tags specific to tags on the track.
	 */
	public enum Track implements Tag {
		TITLE(GenericTag.TITLE, "\"", "\""),
		PERFORMER(GenericTag.ARTIST, "\"", "\"");
		
		private GenericTag tag;
		private String prefix;
		private String suffix;
		
		private Track(GenericTag tag, String prefix, String suffix) {
			this.tag = tag;
			this.prefix = prefix;
			this.suffix = suffix;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public GenericTag getGeneric() {
			return tag;
		}
		
		@Override
		public String toString() {
			return this.name().replaceAll("_", " ");
		}
		
		/**
		 * Returns the name of this tag, that includes
		 * the properly formatted value.
		 * @param value the value for this tag
		 * @return the name and value of this tag in the proper format
		 */
		public String toString(String value) {
			return this.name().replaceAll("_", " ") + " " + prefix + value + suffix;
		}
	}
}
