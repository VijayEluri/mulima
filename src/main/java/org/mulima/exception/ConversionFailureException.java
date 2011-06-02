package org.mulima.exception;

/**
 * Signals a failure to convert 
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class ConversionFailureException extends MulimaException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an empty exception.
	 */
	public ConversionFailureException() {
		super();
	}

	/**
	 * Constructs an exception from parameters.
	 * @param message the message
	 */
	public ConversionFailureException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructs an exception from parameters.
	 * @param cause the cause
	 */
	public ConversionFailureException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Constructs an exception from parameters.
	 * @param message the message
	 * @param cause the cause
	 */
	public ConversionFailureException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
