package org.ajoberstar.mulima.meta;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

  private final String dialect;
  private final Path sourceFile;
  private final Path artworkFile;
  private final Path audioFile;
  private final Map<String, List<String>> tags;
  private final List<CuePoint> cues;
  private final List<Metadata> children;

  private Metadata(String dialect, Path sourceFile, Path artworkFile, Path audioFile, Map<String, List<String>> tags, List<CuePoint> cues, List<Metadata> children) {
    this.dialect = Objects.requireNonNull(dialect, "dialect must not be null");
    this.sourceFile = Objects.requireNonNull(sourceFile, "sourceFile must not be null");
    this.artworkFile = artworkFile;
    this.audioFile = audioFile;
    this.tags = Collections.unmodifiableMap(tags);
    this.cues = Collections.unmodifiableList(cues);
    this.children = Collections.unmodifiableList(children);
  }

  public Path getSourceFile() {
    return sourceFile;
  }

  public Optional<Path> getArtworkFile() {
    return Optional.ofNullable(artworkFile);
  }

  public Optional<Path> getAudioFile() {
    return Optional.ofNullable(audioFile);
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

  public List<CuePoint> getCues() {
    return cues;
  }

  public List<Metadata> getChildren() {
    return children;
  }

  public Metadata denormalize() {
    if (children.isEmpty()) {
      return this;
    } else {
      var builder = Metadata.builder("generic");
      builder.setSourceFile(sourceFile);

      denormalize(this)
          .forEach(builder::addChild);

      return builder.build();
    }
  }

  private Stream<Metadata> denormalize(Metadata parent) {
    var dSource = getSourceFile();
    var dArtwork = getArtworkFile().or(parent::getArtworkFile).orElse(null);
    var dAudio = getAudioFile().or(parent::getAudioFile).orElse(null);
    var dTags = new HashMap<>(parent.getTags());
    dTags.putAll(tags);

    var metadata = new Metadata(dialect, dSource, dArtwork, dAudio, dTags, cues, children);
    if (children.isEmpty()) {
      return Stream.of(metadata);
    } else {
      return children.stream()
          .flatMap(child -> child.denormalize(metadata));
    }
  }

  public Metadata translate(String toDialect) {
    if (dialect.equals(toDialect)) {
      return this;
    } else {
      var translatedTags = new HashMap<String, List<String>>();
      tags.forEach((name, values) -> {
        TAG_MAPPINGS.stream()
            .filter(mapping -> name.equals(mapping.get(dialect)))
            .filter(mapping -> mapping.containsKey(toDialect))
            .map(mapping -> mapping.get(toDialect))
            .findFirst()
            .ifPresent(toName -> translatedTags.put(toName, values));
      });

      var translatedChildren = children.stream()
          .map(child -> child.translate(toDialect))
          .collect(Collectors.toList());

      return new Metadata(toDialect, sourceFile, artworkFile, audioFile, translatedTags, cues, translatedChildren);
    }
  }

  public Metadata merge(Metadata right) {
    // FIXME implement
    return null;
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

  public static Builder builder(String dialect) {
    return new Builder(dialect);
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
    private Path sourceFile = null;
    private Path artworkFile = null;
    private Path audioFile = null;
    private Map<String, List<String>> tags = new HashMap<>();
    private List<CuePoint> cues = new ArrayList<>();
    private List<Builder> children = new ArrayList<>();

    private Builder(String dialect) {
      this.dialect = dialect;
    }

    public Path getSourceFile() {
      return sourceFile;
    }

    public Builder setSourceFile(Path sourceFile) {
      this.sourceFile = sourceFile;
      return this;
    }

    public Optional<Path> getArtworkFile() {
      return Optional.ofNullable(artworkFile);
    }

    public Builder setArtworkFile(Path artworkFile) {
      this.artworkFile = artworkFile;
      return this;
    }

    public Optional<Path> getAudioFile() {
      return Optional.ofNullable(audioFile);
    }

    public Builder setAudioFile(Path audioFile) {
      this.audioFile = audioFile;
      return this;
    }

    public Map<String, List<String>> getTags() {
      return tags;
    }

    public Builder addTag(String name, String value) {
      if (!tags.containsKey(name)) {
        tags.put(name, new ArrayList<>());
      }
      tags.get(name).add(value);
      return this;
    }

    public Builder addAllTags(Map<String, List<String>> tags) {
      tags.forEach((name, values) -> {
        values.forEach(value -> addTag(name, value));
      });
      return this;
    }

    public List<CuePoint> getCues() {
      return cues;
    }

    public Builder addCue(CuePoint cue) {
      cues.add(cue);
      return this;
    }

    public List<Builder> getChildren() {
      return children;
    }

    public Builder newChild() {
      var builder = new Builder(dialect);
      builder.setSourceFile(sourceFile);
      builder.setArtworkFile(artworkFile);
      builder.setAudioFile(audioFile);
      children.add(builder);
      return builder;
    }

    public Builder addChild(Metadata child) {
      var tChild = child.translate(dialect);
      var builder = new Builder(dialect);
      builder.setSourceFile(tChild.sourceFile);
      builder.setArtworkFile(tChild.artworkFile);
      builder.setAudioFile(tChild.audioFile);
      tChild.getCues().forEach(builder::addCue);
      builder.addAllTags(tChild.tags);
      child.getChildren().forEach(builder::addChild);
      children.add(builder);
      return builder;
    }

    public Metadata build() {
      var builtChildren = children.stream()
          .map(Builder::build)
          .collect(Collectors.toList());
      return new Metadata(dialect, sourceFile, artworkFile, audioFile, tags, cues, builtChildren);
    }
  }
}
