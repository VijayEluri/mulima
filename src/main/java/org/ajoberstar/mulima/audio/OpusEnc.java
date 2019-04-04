package org.ajoberstar.mulima.audio;

import java.nio.file.Path;
import java.util.ArrayList;

import org.ajoberstar.mulima.service.ProcessService;

public class OpusEnc implements AudioEncoder {
  private final String path;
  private final int bitrate;
  private final ProcessService process;

  public OpusEnc(String path, int bitrate, ProcessService process) {
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
  public void encode(Path source, Path destination) {
    var command = new ArrayList<String>();
    command.add(path);
    command.add("--bitrate");
    command.add(Integer.toString(bitrate));
    command.add(source.toString());
    command.add(destination.toString());

    process.execute(command).assertSuccess();
  }
}
