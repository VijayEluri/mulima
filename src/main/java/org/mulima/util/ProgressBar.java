package org.mulima.util;

/**
 * Tracks the progress of an operation.
 *
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ProgressBar {
  /**
   * Notify the <code>ProgressBar</code> that another operation has completed.
   *
   * @return the current number of complete operations
   */
  int next();

  /**
   * Notify the <code>ProgressBar</code> that all operations have completed. This is handy to ensure
   * that any completion events occur.
   */
  void done();
}
