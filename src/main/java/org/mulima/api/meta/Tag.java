package org.mulima.api.meta;

/**
 * Represents a tag option for a metadata holder. All tags should be tied to a {@link GenericTag}.
 *
 * @author Andrew Oberstar
 * @see GenericTag
 * @since 0.1.0
 */
public interface Tag {
  /**
   * Gets the <code>GenericTag</code> corresponding to this tag.
   *
   * @return generic form of this tag
   */
  GenericTag getGeneric();
}
