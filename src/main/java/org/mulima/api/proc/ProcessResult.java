package org.mulima.api.proc;

import org.mulima.util.StringUtil;
import java.util.List;

/**
 * Represents the result of a Process execution.  Provides
 * access to the exit value as well as the standard out
 * and standard error output.
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class ProcessResult {
  /**
   * The command that was executed
   */
  private final String command;

  /**
   * The exit value of the process
   */
  private final int exitVal;

  /**
   * The system output of the process
   */
  private final String output;

  /**
   * The system error output of the process
   */
  private final String error;

  public ProcessResult(List<String> command, int exitVal, String output, String error) {
    this(StringUtil.join(command, " "), exitVal, output, error);
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

  /**
   * @return true if the process was successful (exit value
   * of 0), false otherwise
   */
  public boolean isSuccess() {
    return exitVal == 0;
  }
}
