package org.ajoberstar.mulima.meta;

import org.ajoberstar.mulima.service.ProcessResult;
import org.ajoberstar.mulima.service.ProcessService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MetaflacTagger implements MetadataParser, MetadataWriter {
  private static final Pattern REGEX = Pattern.compile("comment\\[[0-9]+\\]: ([A-Za-z]+)=(.+)");

  private final String path;
  private final ExecutorService executor;

  public MetaflacTagger(String path, ExecutorService executor) {
    this.path = path;
    this.executor = executor;
  }

  @Override
  public boolean accepts(Path file) {
    return file.getFileName().toString().endsWith(".flac");
  }

  @Override
  public CompletionStage<Metadata> parse(Path file) {
    return CompletableFuture.supplyAsync(() -> {
              List<String> command = new ArrayList<>();
              command.add(path);
              command.add("--list");
              command.add("--block-type=VORBIS_COMMENT");
              command.add(file.toString());
              return command;
            }).thenComposeAsync(ProcessService::execute, executor)
            .thenApplyAsync(ProcessResult::assertSuccess)
            .thenApplyAsync(result -> {
              var builder = Metadata.builder("vorbis");
              builder.setFile(file);

              result.getOutput().lines()
                      .map(String::trim)
                      .map(REGEX::matcher)
                      .filter(Matcher::matches)
                      .forEach(matcher -> {
                        var name = matcher.group(1);
                        var value = matcher.group(2);
                        builder.addTag(name, value);
                      });
              return builder.build();
            });
  }

  @Override
  public CompletionStage<Void> write(Metadata meta, Path file) {
    return CompletableFuture.supplyAsync(() -> {
      List<String> command = new ArrayList<>();
      command.add(path);
      command.add("--remove-all-tags");

      var translated = meta.translate("vorbis");
      translated.getTags().entrySet().stream()
              .flatMap(entry -> {
                var tag = entry.getKey();
                return entry.getValue().stream()
//                      .map(value -> value.replaceAll("\"", "\\\\\""))
                        .map(value -> String.format("--set-tag=%s=%s", tag, value));
              }).forEach(command::add);

      command.add(file.toString());
      return command;
    }).thenComposeAsync(ProcessService::execute, executor)
            .thenAccept(ProcessResult::assertSuccess);
  }
}
