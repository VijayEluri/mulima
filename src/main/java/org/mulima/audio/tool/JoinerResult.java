package org.mulima.audio.tool;

import java.util.List;

import org.mulima.file.audio.AudioFile;
import org.mulima.future.service.ProcessResult;

/**
 * Represents the result of a joiner operation. Provides access to the process's exit value, the
 * source and destination files.
 * 
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class JoinerResult extends ProcessResult {
  /**
   * The source files
   */
  private final List<AudioFile> source;

  /**
   * The destination file
   */
  private final AudioFile dest;

  /**
   * Constructs a joiner result from a process result.
   * 
   * @param source the source files of the join operation
   * @param dest the destination of the join operation
   * @param result the result of the join process
   */
  public JoinerResult(List<AudioFile> source, AudioFile dest, ProcessResult result) {
    this(source, dest, result.getCommand(), result.getExitVal(), result.getOutput(), result.getError());
  }

  /**
   * Constructs a joiner result from the parameters.
   * 
   * @param source the source files of the join operation
   * @param dest the destination of the join operation
   * @param command the command executed
   * @param exitVal the exit value of the process
   * @param output the std out of the process
   * @param error the std err of the process
   */
  public JoinerResult(List<AudioFile> source, AudioFile dest, String command, int exitVal,
      String output, String error) {
    super(command, exitVal, output, error);
    this.dest = dest;
    this.source = source;
  }

  public List<AudioFile> getSource() {
    return source;
  }

  public AudioFile getDest() {
    return dest;
  }
}
