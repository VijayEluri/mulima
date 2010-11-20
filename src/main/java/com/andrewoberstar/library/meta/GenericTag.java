package com.andrewoberstar.library.meta;

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
		return TagUtil.toCamelCase(this.name());
	}
	
	/**
	 * Returns the <code>GenericTag</code> that has the same camel case
	 * value as <code>arg0</code>.
	 * @param arg0 the camel case name of the constant to return
	 * @return the <code>GenericTag</code> with the specified name 
	 */
	public static GenericTag valueOfCamelCase(String arg0) {
		String name = TagUtil.fromCamelCase(arg0);
		return GenericTag.valueOf(name);
	}
}
