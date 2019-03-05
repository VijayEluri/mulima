package org.mulima.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * An abstract implementation of a metadata object. Provides map-based tag support.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public abstract class AbstractMetadata implements Metadata {
  private final Metadata parent;
  private final Map<GenericTag, List<String>> map = new TreeMap<GenericTag, List<String>>();

  protected AbstractMetadata(Metadata parent) {
    this.parent = parent;
  }

  /** {@inheritDoc} */
  @Override
  public Metadata getParent() {
    return parent;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSet(Tag tag) {
    if (tag == null) {
      return false;
    }
    boolean set = map.containsKey(tag.getGeneric());
    if (!set && parent != null) {
      return parent.isSet(tag);
    } else {
      return set;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void add(Tag tag, String value) {
    if (tag == null || value == null) {
      return;
    }

    GenericTag generic = tag.getGeneric();
    if (!map.containsKey(generic)) {
      map.put(generic, new ArrayList<String>());
    }

    map.get(generic).add(value.trim());
  }

  /** {@inheritDoc} */
  @Override
  public void addAll(Tag tag, List<String> values) {
    if (tag == null || values == null || values.isEmpty()) {
      return;
    }

    GenericTag generic = tag.getGeneric();
    if (!map.containsKey(generic)) {
      map.put(generic, new ArrayList<String>());
    }

    List<String> tagValues = map.get(generic);
    for (String value : values) {
      tagValues.add(value.trim());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void addAll(Metadata meta) {
    throw new UnsupportedOperationException("addAll not supported yet");
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getAll(Tag tag) {
    if (tag == null) {
      return Collections.unmodifiableList(new ArrayList<String>());
    }
    GenericTag generic = tag.getGeneric();
    if (map.containsKey(generic)) {
      return Collections.unmodifiableList(map.get(generic));
    } else if (parent != null) {
      return parent.getAll(tag);
    } else {
      return Collections.unmodifiableList(new ArrayList<String>());
    }
  }

  /** {@inheritDoc} */
  @Override
  public String getFirst(Tag tag) {
    if (tag == null) {
      return null;
    }
    GenericTag generic = tag.getGeneric();
    if (map.containsKey(generic) && !map.get(generic).isEmpty()) {
      return map.get(generic).get(0);
    } else if (parent != null) {
      return parent.getFirst(tag);
    } else {
      return null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public String getFlat(Tag tag) {
    if (tag == null || !isSet(tag)) {
      return null;
    }

    List<String> values = getAll(tag);
    StringBuilder builder = new StringBuilder();
    ListIterator<String> iterator = values.listIterator();
    builder.append(iterator.next());
    while (iterator.hasNext()) {
      if (iterator.nextIndex() == values.size() - 1) {
        builder.append(" & ");
      } else {
        builder.append(", ");
      }
      builder.append(iterator.next());
    }
    return builder.toString();
  }

  /** {@inheritDoc} */
  @Override
  public Map<GenericTag, List<String>> getMap() {
    return Collections.unmodifiableMap(map);
  }

  /** {@inheritDoc} */
  @Override
  public void remove(Tag tag) {
    if (tag == null) {
      return;
    }
    GenericTag generic = tag.getGeneric();
    map.remove(generic);
  }

  /** {@inheritDoc} */
  @Override
  public void removeAll() {
    map.clear();
  }

  /**
   * Cleans up tags by taking the value of any tag that is identical in all children and moving it to
   * this object.
   *
   * @param children list of child metadata objects
   */
  protected void tidy(Set<? extends Metadata> children) {
    for (GenericTag tag : GenericTag.values()) {
      if (GenericTag.DISC_NUMBER.equals(tag) || GenericTag.TRACK_NUMBER.equals(tag)) {
        continue;
      }

      List<String> values = findCommon(tag, children);
      if (!map.containsKey(tag) && values != null) {
        addAll(tag, values);
        for (Metadata child : children) {
          child.remove(tag);
        }
      }
    }
  }

  /**
   * Finds the common value list for the given tag on the given children. This will only find common
   * values if all children have an identical list of values.
   *
   * @param tag the tag to find the common values of
   * @param children list of child metadata objects
   * @return the list of common values or {@code null} if any children have a different value
   */
  private List<String> findCommon(GenericTag tag, Set<? extends Metadata> children) {
    List<String> values = null;
    for (Metadata child : children) {
      List<String> childValues = child.getMap().get(tag);
      if (values == null) {
        values = childValues;
      } else if (!values.equals(childValues)) {
        return null;
      }
    }
    return values;
  }
}
