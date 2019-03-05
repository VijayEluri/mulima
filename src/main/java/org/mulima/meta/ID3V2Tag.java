package org.mulima.meta;

/**
 * Set of tags used for files using ID3v2 tags.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public enum ID3V2Tag implements Tag {
  TALB(GenericTag.ALBUM), TSOA(GenericTag.ALBUM_SORT), TPUB(GenericTag.ORGANIZATION), TDOR(GenericTag.RELEASE_DATE),

  TPE1(GenericTag.ARTIST), TDOP(GenericTag.ARTIST_SORT), TCOM(GenericTag.COMPOSER), TEXT(GenericTag.LYRICIST), TMLC(GenericTag.PERFORMER), TIPL_producer(GenericTag.PRODUCER), TIPL_engineer(GenericTag.ENGINEER), TIPL_mix(GenericTag.MIXER), TPE3(GenericTag.CONDUCTOR),

  TPOS(GenericTag.DISC_NUMBER), TRCK(GenericTag.TRACK_NUMBER), TIT2(GenericTag.TITLE), TSOT(GenericTag.TITLE_SORT), TCON(GenericTag.GENRE), TDRC(GenericTag.DATE), LOCATION(GenericTag.LOCATION), USLT(GenericTag.LYRICS), COMM(GenericTag.DESCRIPTION);

  private final GenericTag tag;

  /**
   * Constructs a tag.
   *
   * @param tag the generic tag this maps to
   */
  ID3V2Tag(GenericTag tag) {
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
   * Conversion method for getting the matching <code>ID3V2Tag</code> for a given <code>GenericTag
   * </code>.
   *
   * @param generic the generic tag to look for
   * @return the ID3v2 tag that corresponds to <code>generic</code>.
   */
  public static ID3V2Tag valueOf(GenericTag generic) {
    for (var tag : ID3V2Tag.values()) {
      if (generic.equals(tag.getGeneric())) {
        return tag;
      }
    }
    return null;
  }
}
