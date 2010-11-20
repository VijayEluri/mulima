package com.andrewoberstar.library.meta;

/**
 * Contains tags specific to FreeDB.
 * 
 * For more information see their website {@link http://www.freedb.org/}.
 */
public enum FreeDbTag implements Tag {
	DISCID(GenericTag.CDDB_ID),
	DARTIST(GenericTag.ARTIST),
	DTITLE(GenericTag.ALBUM),
	DYEAR(GenericTag.DATE),
	DGENRE(GenericTag.GENRE),
	TTITLE(GenericTag.TITLE),
	TRACK_NUM(GenericTag.TRACK_NUMBER);	
	
	private GenericTag tag;
	
	private FreeDbTag(GenericTag tag) {
		this.tag = tag;
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
}
