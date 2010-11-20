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

package com.andrewoberstar.library.meta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds metadata representing a cue sheet.
 * 
 * For more information see {@link http://en.wikipedia.org/wiki/Cue_sheet_(computing)}.
 * @see CueSheetTag
 */
public class CueSheet extends AbstractMetadata {
	private File file;
	private int num;
	private List<CueSheet.Track> tracks = new ArrayList<CueSheet.Track>();
	
	/**
	 * Sets the physical .cue file represented by
	 * this object.
	 * @param file the cue file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Gets the physical .cue file represented by
	 * this object.
	 * @return the cue file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Gets the number of the cue sheet. Should
	 * be equivalent to disc number.
	 * @return the number of the cue sheet
	 */
	public int getNum() {
		return num;
	}

	/**
	 * Sets the number of the cue sheet. Should
	 * be equivalent to disc number.
	 * @param num the number of the cue sheet
	 */
	public void setNum(int num) {
		this.num = num;
	}

	/**
	 * Gets the tracks associated with this cue
	 * sheet.
	 * @return the tracks on this cue sheet
	 */
	public List<CueSheet.Track> getTracks() {
		return tracks;
	}

	/**
	 * Sets the tracks associated with this cue
	 * sheet.
	 * @param tracks the tracks on this cue sheet
	 */
	public void setTracks(List<CueSheet.Track> tracks) {
		this.tracks = tracks;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (!(obj instanceof CueSheet))
			return false;
		
		CueSheet that = (CueSheet) obj;
		return this.getNum() == that.getNum()
			&& this.getTags().equals(that.getTags())
			&& this.getTracks().equals(that.getTracks());
	}
	
	@Override
	public int hashCode() {
		return ("" + Integer.valueOf(getNum()).hashCode() + getTags().hashCode() + getTracks().hashCode()).hashCode();
	}
	
	@Override
	public String toString() {
		return "[num: " + getNum() + ", tags: " + getTags() + ", tracks:" + getTracks() + "]";
	}
	
	/**
	 * 
	 * @author Andy
	 *
	 */
	public static class Track extends AbstractMetadata {
		private int num;
		private List<CueSheet.Index> indices = new ArrayList<CueSheet.Index>();
		
		/**
		 * @return the num
		 */
		public int getNum() {
			return num;
		}

		/**
		 * @param num the num to set
		 */
		public void setNum(int num) {
			this.num = num;
		}

		/**
		 * @return the indices
		 */
		public List<CueSheet.Index> getIndices() {
			return indices;
		}

		/**
		 * @param indices the indices to set
		 */
		public void setIndices(List<CueSheet.Index> indices) {
			this.indices = indices;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			else if (!(obj instanceof CueSheet.Track))
				return false;
			
			CueSheet.Track that = (CueSheet.Track) obj;
			return this.getNum() == that.getNum()
				&& this.getTags().equals(that.getTags())
				&& this.getIndices().equals(that.getIndices());
		}
		
		@Override
		public int hashCode() {
			return ("" + Integer.valueOf(getNum()).hashCode()
				+ getTags().hashCode()
				+ getIndices().hashCode()).hashCode();
		}
		
		@Override
		public String toString() {
			return "[num: " + getNum() + ", tags: " + getTags() + ", indices: " + getIndices();
		}
	}
	
	public static class Index {
		private int num;
		private String time;
		
		/**
		 * @return the num
		 */
		public int getNum() {
			return num;
		}
		
		/**
		 * @param num the num to set
		 */
		public void setNum(int num) {
			this.num = num;
		}
		
		/**
		 * @return the time
		 */
		public String getTime() {
			return time;
		}
		
		/**
		 * @param time the time to set
		 */
		public void setTime(String time) {
			this.time = time;
		}
		
		/**
		 * @return index formatted for CueSheet 
		 */
		public String format() { 
			return String.format("%1$02d %2$s", getNum(), getTime());
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			else if (!(obj instanceof CueSheet.Index))
				return false;
			
			CueSheet.Index that = (CueSheet.Index) obj;
			return this.getNum() == that.getNum()
				&& this.getTime().equals(that.getTime());
		}
		
		@Override
		public int hashCode() {
			return ("" + Integer.valueOf(getNum()).hashCode()
				+ this.getTime().hashCode()).hashCode();
		}
		
		@Override
		public String toString() {
			return "[num: " + getNum() + ", time: " + getTime() + "]";
		}
	}
}
