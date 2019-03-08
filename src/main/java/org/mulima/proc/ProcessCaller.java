package org.mulima.proc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.future.service.ProcessResult;

/**
 * Executes a <code>Process</code>. This is an alternative to {@link ProcessBuilder#start()} and
 * {@link Runtime#exec(String)} that will give you a {@link ProcessResult} object.
 * 
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class ProcessCaller implements Callable<ProcessResult> {
  private static final Logger logger = LogManager.getLogger(ProcessCaller.class);
  private final String description;
  private final List<String> command;
  private final String input;

  /**
   * Constructs a process caller with the specified operating system program and arguments.
   * 
   * @param command the list containing the program and its arguments.
   */
  public ProcessCaller(List<String> command) {
    this(null, command, null);
  }

  /**
   * Constructs a process caller with the specified operating system program and arguments.
   * 
   * @param command the list containing the program and its arguments.
   * @param input the input to this process
   */
  public ProcessCaller(List<String> command, String input) {
    this(null, command, input);
  }

  /**
   * Constructs a process caller with the specified operating system program and arguments.
   * 
   * @param description a description of the process to be executed
   * @param command the list containing the program and its arguments.
   */
  public ProcessCaller(String description, List<String> command) {
    this(description, command, null);
  }

  /**
   * Constructs a process caller with the specified operating system program and arguments.
   * 
   * @param description a description of the process to be executed
   * @param command the list containing the program and its arguments.
   * @param input the input to this process (optional)
   */
  public ProcessCaller(String description, List<String> command, String input) {
    this.description = description;
    this.command = command;
    this.input = input;
  }

  /**
   * Starts a process using the command specified in the constructor.
   * 
   * @return a process result holding the output of the process.
   * @throws FatalMulimaException if there is a problem with the process
   */
  @Override
  public ProcessResult call() {
    try {
      logger.debug("Starting: {}", description);
      logger.debug("Executing command: {}", command);
      var proc = new ProcessBuilder(command).start();

      if (input != null && !input.isBlank()) {
        try (var writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()))) {
          writer.write(input);
        }
      }

      var output = new StringBuilder();
      var error = new StringBuilder();
      // FIXME this is busted
      // proc.waitForProcessOutput(output, error);
      var exit = proc.exitValue();
      logger.debug("Finished: {}", description);
      return new ProcessResult(command, exit, output.toString(), error.toString());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
