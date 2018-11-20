package org.mulima.exception;

/**
 * Signals an exception related to Mulima.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class UncheckedMulimaException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /** Constructs an empty exception. */
  public UncheckedMulimaException() {
    super();
  }

  /**
   * Constructs an exception from parameters.
   *
   * @param message the message
   */
  public UncheckedMulimaException(String arg0) {
    super(arg0);
  }

  /**
   * Constructs an exception from parameters.
   *
   * @param cause the cause
   */
  public UncheckedMulimaException(Throwable arg0) {
    super(arg0);
  }

  /**
   * Constructs an exception from parameters.
   *
   * @param message the message
   * @param cause the cause
   */
  public UncheckedMulimaException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }
}
