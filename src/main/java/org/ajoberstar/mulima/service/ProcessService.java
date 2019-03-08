package org.ajoberstar.mulima.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ProcessService {
  private static final Logger logger = LogManager.getLogger(ProcessService.class);

  private ProcessService() {
    // do not instantiate
  }

  public static CompletableFuture<String> executeForOutput(String... command) {
    var builder = new ProcessBuilder(command);
    return executeForOutput(builder);
  }

  public static CompletableFuture<String> executeForOutput(List<String> command) {
    var builder = new ProcessBuilder(command);
    return executeForOutput(builder);
  }

  public static CompletableFuture<String> executeForOutput(ProcessBuilder builder) {
    return execute(builder)
            .thenApply(result -> {
              if (result.isSuccess()) {
                return result.getOutput();
              } else {
                // FIXME better
                throw new RuntimeException("Command failed: " + result.getExitVal() + "\n" + result.getOutput() + "\n" + result.getError());
              }
            });
  }

  public static CompletableFuture<ProcessResult> execute(String... command) {
    var builder = new ProcessBuilder(command);
    return execute(builder);
  }

  public static CompletableFuture<ProcessResult> execute(List<String> command) {
    var builder = new ProcessBuilder(command);
    return execute(builder);
  }

  public static CompletableFuture<ProcessResult> execute(ProcessBuilder builder) {
    try {
      var process = builder.start();
      var exitCode = CompletableFuture.supplyAsync(handleExitCode(process));
      var output = CompletableFuture.supplyAsync(handleStream(process.getInputStream()));
      var error = CompletableFuture.supplyAsync(handleStream(process.getErrorStream()));

      return exitCode.thenCombineAsync(output, (exitValue, outputStr) -> {
        return new ProcessResult(builder.command(), exitValue, outputStr, "");
      }).thenCombineAsync(error, (result, errorStr) -> {
        return new ProcessResult(result.getCommand(), result.getExitVal(), result.getOutput(), errorStr);
      });
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Supplier<Integer> handleExitCode(Process process) {
    return () -> {
      try {
        return process.waitFor();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return -1;
      }
    };
  }

  private static Supplier<String> handleStream(InputStream inputStream) {
    return () -> {
      try (var outputStream = new ByteArrayOutputStream()) {
        inputStream.transferTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    };
  }
}
