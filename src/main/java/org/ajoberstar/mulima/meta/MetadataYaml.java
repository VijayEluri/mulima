package org.ajoberstar.mulima.meta;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class MetadataYaml implements MetadataParser, MetadataWriter {
  private final ObjectMapper mapper;

  public MetadataYaml() {
    this.mapper = new ObjectMapper(new YAMLFactory());
  }

  @Override
  public boolean accepts(Path file) {
    return file.getFileName().toString().endsWith(".yaml");
  }

  @Override
  public Metadata parse(Path file) {
    try {
      var builder = Metadata.builder("generic");
      builder.setSourceFile(file);
      var map = mapper.readValue(file.toFile(), Map.class);
      fromMap(builder, map);
      return builder.build();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void write(Metadata meta, Path file) {
    try {
      mapper.writeValue(file.toFile(), toMap(meta));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public Map<String, Object> toMap(Metadata metadata) {
    var meta = metadata.translate("generic");
    var cues = meta.getCues().stream()
        .map(cue -> Map.of("index", cue.getIndex(), "time", cue.getTime()))
        .collect(Collectors.toList());
    var map = new LinkedHashMap<String, Object>();
    metadata.getArtworkFile().ifPresent(value -> map.put("artworkFile", value));
    metadata.getAudioFile().ifPresent(value -> map.put("audioFile", value));
    if (!cues.isEmpty()) {
      map.put("cues", cues);
    }
    if (!metadata.getTags().isEmpty()) {
      map.put("tags", metadata.getTags());
    }
    if (!metadata.getChildren().isEmpty()) {
      map.put("children", metadata.getChildren().stream().map(this::toMap).collect(Collectors.toList()));
    }
    return map;
  }

  public void fromMap(Metadata.Builder meta, Map<String, Object> map) {
    meta.setArtworkFile(Optional.ofNullable((String) map.get("artworkFile")).map(this::safeUri).map(Paths::get).orElse(null));
    meta.setAudioFile(Optional.ofNullable((String) map.get("audioFile")).map(this::safeUri).map(Paths::get).orElse(null));

    ((List<Map<String, Object>>) map.getOrDefault("cues", List.of())).forEach(cueMap -> {
      var cue = new CuePoint((int) cueMap.get("index"), (String) cueMap.get("time"));
      meta.addCue(cue);
    });

    ((Map<String, List<String>>) map.getOrDefault("tags", Map.of())).forEach((key, values) -> {
      values.forEach(value -> meta.addTag(key, value));
    });

    ((List<Map<String, Object>>) map.getOrDefault("children", List.of())).forEach(childMap -> {
      var childMeta = meta.newChild();
      fromMap(childMeta, childMap);
    });
  }

  private URI safeUri(String uriStr) {
    try {
      return new URI(uriStr);
    } catch (URISyntaxException e) {
      // TODO better
      throw new RuntimeException(e);
    }
  }
}
