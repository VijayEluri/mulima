package org.ajoberstar.mulima.service;

import java.nio.charset.Charset;
import java.util.List;

public class ProcessResult {
  private final String command;
  private final int exitVal;
  private final byte[] output;
  private final byte[] error;

  public ProcessResult(List<String> command, int exitVal, byte[] output, byte[] error) {
    this(String.join(" ", command), exitVal, output, error);
  }

  public ProcessResult(String command, int exitVal, byte[] output, byte[] error) {
    this.command = command;
    this.exitVal = exitVal;
    this.output = output;
    this.error = error;
  }

  public String getCommand() {
    return command;
  }

  public int getExitVal() {
    return exitVal;
  }

  public String getOutput() {
    return new String(output);
  }

  public String getOutput(Charset charset) {
    return new String(output, charset);
  }

  public String getError() {
    return new String(error);
  }

  public ProcessResult assertSuccess() {
    if (exitVal == 0) {
      return this;
    } else {
      var lines = List.of(
          String.format("Process Failed (%d): %s", exitVal, command.replaceAll("%", "%%")),
          "Output:",
          "-------",
          getOutput(),
          "Error:",
          "------",
          getError());

      throw new IllegalStateException(String.join(System.lineSeparator(), lines));
    }
  }
}
