package com.andrewoberstar.library.meta;

/**
 * Holds metadata associated with a track on an <code>Album</code>.
 * @see Album, Disc
 */
public class Track extends AbstractMetadata implements Metadata {
	private CueRef cueRef = null;
		
	/**
	 * Gets the value of {@link GenericTag#TRACK_NUMBER} 
	 * @return the number of the track
	 */
	public int getNum() {
		return Integer.valueOf(getTags().getFirst(GenericTag.TRACK_NUMBER));
	}

	/**
	 * Gets the reference to the <code>CueSheet</code>.
	 * @return the cueRef for this track
	 */
	public CueRef getCueRef() {
		return cueRef;
	}

	/**
	 * Sets the reference to the <code>CueSheet</code>.
	 * @param cueRef the cueRef to set for this track
	 */
	public void setCueRef(CueRef cueRef) {
		this.cueRef = cueRef;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (!(obj instanceof Track))
			return false;
		
		Track that = (Track) obj;
		return this.getTags().equals(that.getTags()) 
			&& (this.getCueRef() == that.getCueRef() || this.getCueRef().equals(that.getCueRef()));
	}
	
	@Override
	public int hashCode() {
		return ("" + getTags().hashCode() 
			+ (getCueRef() == null ? "" : getCueRef().hashCode())).hashCode();
	}
	
	@Override
	public String toString() {
		return "[tags: " + getTags().toString() + ", cueRef: " + (getCueRef() == null ? "null" : getCueRef().toString()) + "]";
	}

	/**
	 * A reference to the <code>CueSheet</code> for this album. This
	 * can be used for songs that are spread across multiple tracks
	 * in the <code>CueSheet</code>.
	 * 
	 * @see CueSheet
	 */
	public static class CueRef {
		private int cueNum;
		private int startNum;
		private int endNum;
		
		/**
		 * @return the number of the <code>CueSheet</code> this reference
		 * corresponds to.
		 */
		public int getCueNum() {
			return cueNum;
		}

		/**
		 * @param cueNum the number of the <code>CueSheet</code> this reference
		 * corresponds to.
		 */
		public void setCueNum(int cueNum) {
			this.cueNum = cueNum;
		}

		/**
		 * @return the beginning track number on the <code>CueSheet</code>.
		 */
		public int getStartNum() {
			return startNum;
		}

		/**
		 * @param startNum the beginning track number on the <code>CueSheet</code>.
		 */
		public void setStartNum(int startNum) {
			this.startNum = startNum;
		}

		/**
		 * @return the ending track number on the <code>CueSheet</code>.
		 */
		public int getEndNum() {
			return endNum;
		}

		/**
		 * @param endNum the ending track number on the <code>CueSheet</code>.
		 */
		public void setEndNum(int endNum) {
			this.endNum = endNum;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			else if (!(obj instanceof CueRef))
				return false;
			
			CueRef that = (CueRef) obj;
			return this.cueNum == that.cueNum 
				&& this.startNum == that.startNum 
				&& this.endNum == that.endNum;
		}
		
		@Override
		public int hashCode() {
			return ("" + Integer.valueOf(cueNum).hashCode()
				+ Integer.valueOf(startNum).hashCode()
				+ Integer.valueOf(endNum).hashCode()).hashCode();
		}
		
		@Override
		public String toString() {
			return "[cueNum: " + getCueNum() + ", startNum: " + getStartNum() + ", endNum: " + getEndNum() + "]";
		}
	}
}
