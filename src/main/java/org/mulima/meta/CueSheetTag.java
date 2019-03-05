package org.mulima.meta;

/**
 * Set of tags that are used in {@link CueSheet}s.
 *
 * <p>
 * For more information see {@link http://en.wikipedia.org/wiki/Cue_sheet_(computing)}.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class CueSheetTag {
  /** Contains tags specific to the cue sheet as a whole. */
  public enum Cue implements Tag {
    TITLE(GenericTag.ALBUM, "\"", "\""), PERFORMER(GenericTag.ARTIST, "\"", "\""), FILE(GenericTag.FILE, "\"", "\" WAVE"), REM_DATE(GenericTag.RELEASE_DATE, "", ""), REM_GENRE(GenericTag.GENRE, "\"", "\""), REM_DISCID(GenericTag.CDDB_ID, "", "");

    private GenericTag tag;
    private String prefix;
    private String suffix;

    /**
     * Constructs a tag.
     *
     * @param tag the generic tag this maps to
     * @param prefix the string to prepend to values of this tag
     * @param suffix the string append to values of this tag
     */
    Cue(GenericTag tag, String prefix, String suffix) {
      this.tag = tag;
      this.prefix = prefix;
      this.suffix = suffix;
    }

    /** {@inheritDoc} */
    @Override
    public GenericTag getGeneric() {
      return tag;
    }

    @Override
    public String toString() {
      return this.name().replaceAll("_", " ");
    }

    /**
     * Returns the name of this tag, that includes the properly formatted value.
     *
     * @param value the value for this tag
     * @return the name and value of this tag in the proper format
     */
    public String toString(String value) {
      return this.toString() + " " + prefix + value + suffix;
    }
  }

  /** Contains tags specific to tags on the track. */
  public enum Track implements Tag {
    TITLE(GenericTag.TITLE, "\"", "\""), PERFORMER(GenericTag.ARTIST, "\"", "\"");

    private GenericTag tag;
    private String prefix;
    private String suffix;

    /**
     * Constructs a tag.
     *
     * @param tag the generic tag this maps to
     * @param prefix the string to prepend to values of this tag
     * @param suffix the string append to values of this tag
     */
    Track(GenericTag tag, String prefix, String suffix) {
      this.tag = tag;
      this.prefix = prefix;
      this.suffix = suffix;
    }

    /** {@inheritDoc} */
    @Override
    public GenericTag getGeneric() {
      return tag;
    }

    @Override
    public String toString() {
      return this.name().replaceAll("_", " ");
    }

    /**
     * Returns the name of this tag, that includes the properly formatted value.
     *
     * @param value the value for this tag
     * @return the name and value of this tag in the proper format
     */
    public String toString(String value) {
      return this.name().replaceAll("_", " ") + " " + prefix + value + suffix;
    }
  }
}
