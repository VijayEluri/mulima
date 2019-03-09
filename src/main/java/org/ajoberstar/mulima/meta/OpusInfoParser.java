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

public final class OpusInfoParser implements MetadataParser {
  private static final Pattern REGEX = Pattern.compile("([A-Za-z]+)=(.+)");

  private final String path;
  private final ProcessService process;

  public OpusInfoParser(String path, ProcessService process) {
    this.path = path;
    this.process = process;
  }

  @Override
  public boolean accepts(Path file) {
    return file.getFileName().toString().endsWith(".opus");
  }

  @Override
  public CompletionStage<Metadata> parse(Path file) {
      List<String> command = new ArrayList<>();
      command.add(path);
      command.add(file.toString());
    return process.execute(command)
            .thenApplyAsync(ProcessResult::assertSuccess)
            .thenApplyAsync(result -> {
              var builder = Metadata.builder("vorbis");
              builder.setFile(file);

              result.getOutput().lines()
                      .map(String::trim)
                      .dropWhile(line -> !"User comments section follows...".equals(line))
                      .skip(1)
                      .map(REGEX::matcher)
                      .takeWhile(Matcher::matches)
                      .forEach(matcher -> {
                        var name = matcher.group(1);
                        var value = matcher.group(2);
                        builder.addTag(name, value);
                      });
              return builder.build();
            });
  }
}
