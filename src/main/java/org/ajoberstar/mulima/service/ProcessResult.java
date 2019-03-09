package org.ajoberstar.mulima.service;

import java.util.List;

public class ProcessResult {
  private final String command;
  private final int exitVal;
  private final String output;
  private final String error;

  public ProcessResult(List<String> command, int exitVal, String output, String error) {
    this(String.join(" ", command), exitVal, output, error);
  }

  public ProcessResult(String command, int exitVal, String output, String error) {
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
    return output;
  }

  public String getError() {
    return error;
  }

  public ProcessResult assertSuccess() {
    if (exitVal == 0) {
      return this;
    } else {
      var lines = List.of(
          String.format("Process Failed (%d): %s", exitVal, command.replaceAll("%", "%%")),
          "Output:",
          "-------",
          output,
          "Error:",
          "------",
          error);

      throw new IllegalStateException(String.join(System.lineSeparator(), lines));
    }
  }
}
