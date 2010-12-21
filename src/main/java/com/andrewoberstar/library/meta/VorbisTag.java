package com.andrewoberstar.library.meta;

public enum VorbisTag implements Tag {
	ALBUM(GenericTag.ALBUM),
	ALBUMSORT(GenericTag.ALBUM_SORT),
	ORGANIZATION(GenericTag.ORGANIZATION),
	PUBLISHER(GenericTag.PUBLISHER),
	PRODUCTNUMBER(GenericTag.PRODUCT_NUMBER),
	CATALOGNUMBER(GenericTag.CATALOG_NUMBER),
	VOLUME(GenericTag.VOLUME),
	RELEASE_DATE(GenericTag.RELEASE_DATE),

	ARTIST(GenericTag.ARTIST),
	ARTISTSORT(GenericTag.ARTIST_SORT),
	GUEST_ARTIST(GenericTag.GUEST_ARTIST),
	COMPOSER(GenericTag.COMPOSER),
	LYRICIST(GenericTag.LYRICIST),
	PERFORMER(GenericTag.PERFORMER),
	PRODUCER(GenericTag.PRODUCER),
	ENGINEER(GenericTag.ENGINEER),
	CONDUCTOR(GenericTag.CONDUCTOR),
	ENSEMBLE(GenericTag.ENSEMBLE),

	DISCNUMBER(GenericTag.DISC_NUMBER),
	TRACKNUMBER(GenericTag.TRACK_NUMBER),
	TITLE(GenericTag.TITLE),
	TITLESORT(GenericTag.TITLE_SORT),
	PART(GenericTag.PART),
	VERSION(GenericTag.VERSION),
	OPUS(GenericTag.OPUS),
	GENRE(GenericTag.GENRE),
	DATE(GenericTag.DATE),
	LOCATION(GenericTag.LOCATION),
	LYRICS(GenericTag.LYRICS),
	DESCRIPTION(GenericTag.DESCRIPTION);
	
	private final GenericTag tag;
	
	private VorbisTag(GenericTag tag) {
		this.tag = tag;
	}

	@Override
	public GenericTag getGeneric() {
		return tag;
	}
	
	@Override
	public String toString() {
		return this.name().replaceAll("_", " ");
	}
	
	public static VorbisTag valueOf(GenericTag generic) {
		for (VorbisTag tag : VorbisTag.values()) {
			if (generic.equals(tag.getGeneric())) {
				return tag;
			}
		}
		return null;
	}
}
