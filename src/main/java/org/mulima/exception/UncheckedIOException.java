package org.mulima.exception;

/**
 * Signals a fatal IO exception.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class UncheckedIOException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /** Constructs an empty exception. */
  public UncheckedIOException() {
    super();
  }

  /**
   * Constructs an exception from parameters.
   *
   * @param message the message
   */
  public UncheckedIOException(String message) {
    super(message);
  }

  /**
   * Constructs an exception from parameters.
   *
   * @param cause the cause
   */
  public UncheckedIOException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs an exception from parameters.
   *
   * @param message the message
   * @param cause the cause
   */
  public UncheckedIOException(String message, Throwable cause) {
    super(message, cause);
  }
}
