package org.mulima.audio.tool;

import org.mulima.file.audio.AudioFile;
import org.mulima.proc.ProcessResult;

/**
 * Represents the result of a tagger operation. Provides access to the process's exit value, the
 * file and metadata.
 * 
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class TaggerResult extends ProcessResult {
  /**
   * The file to read or write to
   */
  private final AudioFile file;

  /**
   * Constructs a tagger result from a process result.
   * 
   * @param file the file read from or written to
   * @param result the result of the tagger process
   */
  public TaggerResult(AudioFile file, ProcessResult result) {
    this(file, result.getCommand(), result.getExitVal(), result.getOutput(), result.getError());
  }

  /**
   * Constructs a tagger result from the parameters.
   * 
   * @param file the file read from or written to
   * @param command the command executed
   * @param exitVal the exit value of the process
   * @param output the std out of the process
   * @param error the std err of the process
   */
  public TaggerResult(AudioFile file, String command, int exitVal, String output, String error) {
    super(command, exitVal, output, error);
    this.file = file;
  }

  public AudioFile getFile() {
    return file;
  }
}
