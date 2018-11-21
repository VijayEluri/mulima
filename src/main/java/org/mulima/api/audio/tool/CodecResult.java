package org.mulima.api.audio.tool;

import org.mulima.api.file.audio.AudioFile;
import org.mulima.api.proc.ProcessResult;

/**
 * Represents the result of a codec operation. Provides access to the process's exit value, the
 * source and destination files.
 * 
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class CodecResult extends ProcessResult {
  /**
   * The source of the operation
   */
  private final AudioFile source;

  /**
   * The destination for the operation
   */
  private final AudioFile dest;

  /**
   * Constructs a codec result from a process result.
   * 
   * @param source the source of the codec operation
   * @param dest the destination of the codec operation
   * @param result the result of the codec process
   */
  public CodecResult(AudioFile source, AudioFile dest, ProcessResult result) {
    this(source, dest, result.getCommand(), result.getExitVal(), result.getOutput(), result.getError());
  }

  /**
   * Constructs a codec result from the parameters.
   * 
   * @param source the source of the codec operation
   * @param dest the destination of the codec operation
   * @param command the command executed
   * @param exitVal the exit value of the process
   * @param output the std out of the process
   * @param error the std err of the process
   */
  public CodecResult(AudioFile source, AudioFile dest, String command, int exitVal, String output, String error) {
    super(command, exitVal, output, error);
    this.source = source;
    this.dest = dest;
  }

  public AudioFile getSource() {
    return source;
  }

  public AudioFile getDest() {
    return dest;
  }
}
