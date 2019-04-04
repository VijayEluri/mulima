package org.ajoberstar.mulima.meta;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Metadata {
  private static final List<Map<String, String>> TAG_MAPPINGS;

  static {
    try (var stream = Metadata.class.getResourceAsStream("/org/ajoberstar/mulima/meta/tags.csv")) {
      var contents = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
      var headers = contents.lines().findFirst()
          .map(line -> line.split(","))
          .orElse(new String[0]);

      TAG_MAPPINGS = contents.lines()
          .skip(1)
          .map(line -> {
            var row = new HashMap<String, String>();
            var fields = line.split(",");
            for (var i = 0; i < fields.length; i++) {
              row.put(headers[i], fields[i]);
            }
            return row;
          }).collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private final Map<String, List<String>> tags;

  private Metadata(Map<String, List<String>> tags) {
    this.tags = Collections.unmodifiableMap(tags);
  }

  public Map<String, List<String>> getTags() {
    return tags;
  }

  public Optional<String> getTagValue(String tag) {
    if (tags.containsKey(tag)) {
      var value = tags.get(tag).stream()
          .collect(Collectors.joining(", "));
      return Optional.of(value);
    } else {
      return Optional.empty();
    }
  }

  public Map<String, List<String>> translateTags(String toDialect) {
    if ("generic".equals(toDialect)) {
      return tags;
    } else {
      return tags.entrySet().stream()
          .flatMap(entry ->
            getTagMapping("generic", toDialect, entry.getKey())
                .map(tagName -> Map.entry(tagName, entry.getValue()))
                .stream())
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
  }

  @Override
  public boolean equals(Object that) {
    return EqualsBuilder.reflectionEquals(this, that);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public Builder copy() {
    return new Builder("generic", new HashMap<>(tags));
  }

  public static Builder builder(String dialect) {
    return new Builder(dialect, new HashMap<>());
  }

  private static Optional<String> getTagMapping(String fromDialect, String toDialect, String fromName) {
    return TAG_MAPPINGS.stream()
        .filter(mapping -> fromName.equals(mapping.get(fromDialect)))
        .filter(mapping -> mapping.containsKey(toDialect))
        .map(mapping -> mapping.get(toDialect))
        .findFirst();
  }

  public static class Builder {
    private String dialect;
    private Map<String, List<String>> tags;

    private Builder(String dialect, Map<String, List<String>> tags) {
      this.dialect = dialect;
      this.tags = tags;
    }

    public Builder addTag(String name, String value) {
      getTagMapping(dialect, "generic", name).ifPresent(tagName -> {
        if (!tags.containsKey(tagName)) {
          tags.put(tagName, new ArrayList<>());
        }
        tags.get(tagName).add(value);
      });
      return this;
    }

//    public Builder addAllTags(Map<String, List<String>> tags) {
//      tags.forEach((name, values) -> {
//        values.forEach(value -> addTag(name, value));
//      });
//      return this;
//    }

    public Metadata build() {
      return new Metadata(tags);
    }
  }
}
