package org.mulima.meta;

/**
 * Set of tags used for iTunes AAC files.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public enum ITunesTag implements Tag {
  album(GenericTag.ALBUM), sortalbum(GenericTag.ALBUM_SORT), label(GenericTag.ORGANIZATION),

  artist(GenericTag.ARTIST), sortartist(GenericTag.ARTIST_SORT), composer(GenericTag.COMPOSER),

  disc(GenericTag.DISC_NUMBER), track(GenericTag.TRACK_NUMBER), title(GenericTag.TITLE), sorttitle(GenericTag.TITLE_SORT), genre(GenericTag.GENRE), year(GenericTag.DATE), lyrics(GenericTag.LYRICS), comment(GenericTag.DESCRIPTION);

  private final GenericTag tag;

  /**
   * Constructs a tag.
   *
   * @param tag the generic tag this maps to
   */
  ITunesTag(GenericTag tag) {
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
    return this.name().replaceAll("_", ":");
  }

  /**
   * Conversion method for getting the matching <code>ITunesTag</code> for a given <code>GenericTag
   * </code>.
   *
   * @param generic the generic tag to look for
   * @return the iTunes tag that corresponds to <code>generic</code>.
   */
  public static ITunesTag valueOf(GenericTag generic) {
    for (var tag : ITunesTag.values()) {
      if (generic.equals(tag.getGeneric())) {
        return tag;
      }
    }
    return null;
  }
}
