package org.mulima.future.meta;

import org.mulima.audio.tool.TaggerResult;
import org.mulima.file.audio.AudioFile;
import org.mulima.file.audio.AudioFormat;
import org.mulima.future.service.ProcessResult;
import org.mulima.future.service.ProcessService;
import org.mulima.meta.Track;
import org.mulima.meta.VorbisTag;
import org.mulima.proc.ProcessCaller;
import org.mulima.util.FileUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetaflacTagger implements MetadataParser, MetadataWriter {
  private static final Pattern REGEX = Pattern.compile("comment\\[[0-9]+\\]: ([A-Za-z]+)=(.+)");
  // private final Logger logger = LogManager.getLogger(getClass());

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
            }).thenComposeAsync(ProcessService::execute)
            .thenApplyAsync(ProcessResult::assertSuccess)
            .thenApplyAsync(result -> {
              var builder = Metadata.builder("vorbis");
              builder.setFile(file);

              result.getOutput().lines()
                      .map(String::trim)
                      .map(REGEX::matcher)
                      .filter(Matcher::matches)
                      .forEach(matcher -> {
                        var name = matcher.group(1).toUpperCase();
                        var value = matcher.group(2);
                        builder.addTag(name, value);
                      });
              return builder.build();
            }, executor);
  }

  @Override
  public CompletionStage<Void> write(Metadata meta, Path file) {
    return CompletableFuture.supplyAsync(() -> {
      List<String> command = new ArrayList<>();
      command.add(path);
      command.add("--remove-all-tags");

      Metadata translated = meta.translate("vorbis");
      translated.getTags().entrySet().stream()
              .flatMap(entry -> {
                var tag = entry.getKey();
                return entry.getValue().stream()
//                      .map(value -> value.replaceAll("\"", "\\\\\""))
                        .map(value -> String.format("--set-tag=%s=%s", tag, value));
              }).forEach(command::add);

      command.add(file.toString());
      return command;
    }).thenComposeAsync(ProcessService::execute).thenAccept(ProcessResult::assertSuccess);
  }
}
