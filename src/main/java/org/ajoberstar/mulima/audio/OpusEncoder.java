package org.ajoberstar.mulima.audio;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

import org.ajoberstar.mulima.service.ProcessResult;
import org.ajoberstar.mulima.service.ProcessService;

public class OpusEncoder implements AudioEncoder {
  private final String path;
  private final int bitrate;
  private final ProcessService process;

  public OpusEncoder(String path, int bitrate, ProcessService process) {
    this.path = path;
    this.bitrate = bitrate;
    this.process = process;
  }

  @Override
  public boolean acceptsEncode(Path source, Path destination) {
    return (source.getFileName().toString().endsWith(".wav")
        || source.getFileName().toString().endsWith(".flac"))
        && destination.getFileName().toString().endsWith(".opus");
  }

  @Override
  public CompletionStage<Void> encode(Path source, Path destination) {
    var command = new ArrayList<String>();
    command.add(path);
    command.add("--bitrate");
    command.add(Integer.toString(bitrate));
    command.add(source.toString());
    command.add(destination.toString());

    return process.execute(command)
        .thenAccept(ProcessResult::assertSuccess);
  }
}
