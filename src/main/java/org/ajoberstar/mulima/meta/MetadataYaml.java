package org.ajoberstar.mulima.meta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class MetadataYaml implements MetadataParser, MetadataWriter {
  private final ObjectMapper mapper;

  public MetadataYaml() {
    this.mapper = new ObjectMapper(new YAMLFactory());
  }

  @Override public boolean accepts(Path file) {
    return file.endsWith("metadata.yaml");
  }

  @Override public CompletionStage<Metadata> parse(Path file) {
    return null;
  }

  @Override public CompletionStage<Void> write(Metadata meta, Path file) {

    return CompletableFuture.runAsync(() -> {
      try {
        mapper.writeValue(file.toFile(), toMap(meta));
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });
  }

  public Map<String, Object> toMap(Metadata metadata) {
    var meta = metadata.translate("generic");
    var cues = meta.getCues().stream()
        .map(cue -> Map.of("index", cue.getIndex(), "time", cue.getTime()))
            .collect(Collectors.toList());
    var map = new LinkedHashMap<String, Object>();
    map.put("sourceFile", metadata.getSourceFile().orElse(null));
    map.put("artworkFile", metadata.getArtworkFile().orElse(null));
    map.put("audioFile", metadata.getAudioFile().orElse(null));
    map.put("cues", cues);
    map.put("tags", metadata.getTags());
    map.put("children", metadata.getChildren().stream().map(this::toMap).collect(Collectors.toList()));
    return map;
  }

  public Metadata fromList(List<Map<String, Object>> map) {
    // TODO implement
    return null;
  }
}
