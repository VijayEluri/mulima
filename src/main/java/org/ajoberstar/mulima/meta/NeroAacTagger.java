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

public final class NeroAacTagger implements MetadataParser, MetadataWriter {
  private static final Pattern REGEX = Pattern.compile("([A-Za-z]+) = (.+)");

  private final String path;
  private final ProcessService process;

  public NeroAacTagger(String path, ProcessService process) {
    this.path = path;
    this.process = process;
  }

  @Override
  public boolean accepts(Path file) {
    return file.getFileName().toString().endsWith(".m4a");
  }

  @Override
  public CompletionStage<Metadata> parse(Path file) {
      List<String> command = new ArrayList<>();
      command.add(path);
      command.add(file.toString());
      command.add("-list-meta");
    return process.execute(command)
            .thenApplyAsync(ProcessResult::assertSuccess)
            .thenApplyAsync(result -> {
              var builder = Metadata.builder("id3v24");
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
      List<String> command = new ArrayList<>();
      command.add(path);
      command.add(file.toString());

      var translated = meta.translate("id3v24");
      translated.getTags().entrySet().stream()
              .flatMap(entry -> {
                  var tag = entry.getKey();
                  return entry.getValue().stream()
                          .map(value -> String.format("-meta-user:%s=%s", tag, value));
              }).forEach(command::add);

    return process.execute(command)
            .thenAccept(ProcessResult::assertSuccess);
  }
}
