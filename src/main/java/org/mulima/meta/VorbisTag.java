package org.mulima.meta;

/**
 * Set of tags used for Ogg Vorbis and FLAC files.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public enum VorbisTag implements Tag {
  ALBUM(GenericTag.ALBUM), ALBUMSORT(GenericTag.ALBUM_SORT), ORGANIZATION(GenericTag.ORGANIZATION), PUBLISHER(GenericTag.PUBLISHER), PRODUCTNUMBER(GenericTag.PRODUCT_NUMBER), CATALOGNUMBER(GenericTag.CATALOG_NUMBER), VOLUME(GenericTag.VOLUME), RELEASE_DATE(GenericTag.RELEASE_DATE),

  ARTIST(GenericTag.ARTIST), ARTISTSORT(GenericTag.ARTIST_SORT), GUEST_ARTIST(GenericTag.GUEST_ARTIST), COMPOSER(GenericTag.COMPOSER), LYRICIST(GenericTag.LYRICIST), PERFORMER(GenericTag.PERFORMER), PRODUCER(GenericTag.PRODUCER), ENGINEER(GenericTag.ENGINEER), MIXER(GenericTag.MIXER), CONDUCTOR(GenericTag.CONDUCTOR), ENSEMBLE(GenericTag.ENSEMBLE),

  DISCNUMBER(GenericTag.DISC_NUMBER), TRACKNUMBER(GenericTag.TRACK_NUMBER), TITLE(GenericTag.TITLE), TITLESORT(GenericTag.TITLE_SORT), PART(GenericTag.PART), VERSION(GenericTag.VERSION), OPUS(GenericTag.OPUS), GENRE(GenericTag.GENRE), DATE(GenericTag.DATE), LOCATION(GenericTag.LOCATION), LYRICS(GenericTag.LYRICS), DESCRIPTION(GenericTag.DESCRIPTION);

  private final GenericTag tag;

  /**
   * Constructs a tag.
   *
   * @param tag the generic tag this maps to
   */
  private VorbisTag(GenericTag tag) {
    this.tag = tag;
  }

  /** {@inheritDoc} */
  @Override
  public GenericTag getGeneric() {
    return tag;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.name().replaceAll("_", " ");
  }

  /**
   * Conversion method for getting the matching <code>VorbisTag</code> for a given <code>GenericTag
   * </code>.
   *
   * @param generic the generic tag to look for
   * @return the Vorbis tag that corresponds to <code>generic</code>.
   */
  public static VorbisTag valueOf(GenericTag generic) {
    for (VorbisTag tag : VorbisTag.values()) {
      if (generic.equals(tag.getGeneric())) {
        return tag;
      }
    }
    return null;
  }
}
