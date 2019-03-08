package org.mulima.audio.tool;

import java.util.Set;

import org.mulima.file.audio.DiscFile;
import org.mulima.file.audio.TrackFile;
import org.ajoberstar.mulima.service.ProcessResult;

/**
 * Represents the result of a splitter operation. Provides access to the process's exit value, the
 * source and destination files.
 * 
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class SplitterResult extends ProcessResult {
  /**
   * The source file
   */
  private final DiscFile source;

  /**
   * The resulting files
   */
  private final Set<TrackFile> dest;

  /**
   * Constructs a splitter result from a process result.
   * 
   * @param source the source of the split operation
   * @param dest the destination files of the split operation
   * @param result the result of the split process
   */
  public SplitterResult(DiscFile source, Set<TrackFile> dest, ProcessResult result) {
    this(source, dest, result.getCommand(), result.getExitVal(), result.getOutput(), result.getError());
  }

  /**
   * Constructs a splitter result from the parameters.
   * 
   * @param source the source of the split operation
   * @param dest the destination files of the split operation
   * @param command the command executed
   * @param exitVal the exit value of the process
   * @param output the std out of the process
   * @param error the std err of the process
   */
  public SplitterResult(DiscFile source, Set<TrackFile> dest, String command, int exitVal, String output,
      String error) {
    super(command, exitVal, output, error);
    this.source = source;
    this.dest = dest;
  }

  public DiscFile getSource() {
    return source;
  }

  public Set<TrackFile> getDest() {
    return dest;
  }
}
