package org.ajoberstar.mulima.service;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class FileMergeService {
  private final String path;
  private final ProcessService process;

  public FileMergeService(String path, ProcessService process) {
    this.path = path;
    this.process = process;
  }

  public CompletionStage<Void> merge(Path left, Path right, Path merged) {
    var command = List.of(path, left.toString(), right.toString(), "/mergeoutput=" + merged.toString());
    return process.execute(command)
        .thenAccept(ProcessResult::assertSuccess);
  }
}
