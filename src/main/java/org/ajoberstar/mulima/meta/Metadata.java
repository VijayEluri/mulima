package org.ajoberstar.mulima.meta;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Metadata {
    private static final Logger logger = LogManager.getLogger(Metadata.class);
    private static final List<Map<String, String>> TAG_MAPPINGS;

    static {
        try (var stream = Metadata.class.getResourceAsStream("/org/mulima/meta/tags.csv")) {
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
            logger.debug("Loaded tag mappings: {}", TAG_MAPPINGS);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final String dialect;
    private final Path file;
    private final Map<String, List<String>> tags;
    private final List<CuePoint> cues;
    private final List<Metadata> children;

    private Metadata(String dialect, Path file, Map<String, List<String>> tags, List<CuePoint> cues, List<Metadata> children) {
        this.dialect = dialect;
        this.file = file;
        this.tags = Collections.unmodifiableMap(tags);
        this.cues = Collections.unmodifiableList(cues);
        this.children = Collections.unmodifiableList(children);
    }

    public Optional<Path> getFile() {
        return Optional.ofNullable(file);
    }

    public Map<String, List<String>> getTags() {
        return tags;
    }

    public List<CuePoint> getCues() {
        return cues;
    }

    public List<Metadata> getChildren() {
        return children;
    }

    public List<Metadata> denormalize() {
        return denormalize(Collections.emptyMap())
                .collect(Collectors.toList());
    }

    private Stream<Metadata> denormalize(Map<String, List<String>> parentTags) {
        var dTags = new HashMap<String, List<String>>(parentTags);
        dTags.putAll(tags);
        if (children.isEmpty()) {
            return Stream.of(new Metadata(dialect, null, dTags, cues, children));
        } else {
            return children.stream()
                    .flatMap(child -> child.denormalize(dTags));
        }
    }

    public Metadata translate(String toDialect) {
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

        return new Metadata(toDialect, file, translatedTags, cues, translatedChildren);
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
        private Path file = null;
        private Map<String, List<String>> tags = new HashMap<>();
        private List<CuePoint> cues = new ArrayList<>();
        private List<Builder> children = new ArrayList<>();

        private Builder(String dialect) {
            this.dialect = dialect;
        }

        public Optional<Path> getFile() {
            return Optional.ofNullable(file);
        }

        public Builder setFile(Path file) {
            this.file = file;
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
            Builder builder = new Builder(dialect);
            builder.setFile(file);
            children.add(builder);
            return builder;
        }

        public Metadata build() {
            var builtChildren = children.stream()
                    .map(Builder::build)
                    .collect(Collectors.toList());
            return new Metadata(dialect, file, tags, cues, builtChildren);
        }
    }
}
