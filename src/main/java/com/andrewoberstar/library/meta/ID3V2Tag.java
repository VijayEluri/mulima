package com.andrewoberstar.library.meta;

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

	@Override
	public GenericTag getGeneric() {
		return tag;
	}
	
	@Override
	public String toString() {
		return this.name().replaceAll("_", ":");
	}
	
	public static ID3V2Tag valueOf(GenericTag generic) {
		for (ID3V2Tag tag : ID3V2Tag.values()) {
			if (generic.equals(tag.getGeneric())) {
				return tag;
			}
		}
		return null;
	}
}
