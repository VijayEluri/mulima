package com.andrewoberstar.library.meta;

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
