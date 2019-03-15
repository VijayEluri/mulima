package org.ajoberstar.mulima.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.ajoberstar.mulima.flow.Flows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ProcessService implements AutoCloseable {
  private static final Logger logger = LogManager.getLogger(ProcessService.class);

  private final ExecutorService executor;

  public ProcessService(int poolSize) {
    this.executor = Flows.newExecutorService("process-service", poolSize);
  }

  public ProcessResult execute(String... command) {
    var builder = new ProcessBuilder(command);
    return execute(builder);
  }

  public ProcessResult execute(List<String> command) {
    var builder = new ProcessBuilder(command);
    return execute(builder);
  }

  public ProcessResult execute(List<String> command, String inputStr) {
    var builder = new ProcessBuilder(command);
    return execute(builder, inputStr);
  }

  public ProcessResult execute(ProcessBuilder builder) {
    return execute(builder, null);
  }

  public ProcessResult execute(ProcessBuilder builder, String inputStr) {
    try {
      var process = builder.start();
      var exitCode = process.onExit().thenApply(Process::exitValue);
      var output = CompletableFuture.supplyAsync(handleStream(process.getInputStream()));
      var error = CompletableFuture.supplyAsync(handleStream(process.getErrorStream()));
      var input = CompletableFuture.runAsync(() -> writeInput(process.getOutputStream(), inputStr));

      CompletableFuture.allOf(exitCode, output, error, input).join();

      return new ProcessResult(builder.command(), exitCode.join(), output.join(), error.join());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Supplier<String> handleStream(InputStream stream) {
    return () -> {
      try (var outputStream = new ByteArrayOutputStream(); var inputStream = stream) {
        inputStream.transferTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
      } catch (IOException e) {
        logger.debug("Problem handling output stream for process.", e);
        return "";
      }
    };
  }

  private void writeInput(OutputStream stream, String input) {
    if (input == null || input.isBlank()) {
      // no input provided
    } else {
      try (var inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)); var outputStream = stream) {
        inputStream.transferTo(outputStream);
      } catch (IOException e) {
        logger.debug("Problem writing input for process.", e);
      }
    }
  }

  @Override
  public void close() {
    executor.shutdown();
  }
}
