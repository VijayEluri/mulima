package com.andrewoberstar.library.exception;

public class ProcessFailureException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProcessFailureException() {
		super();
	}

	public ProcessFailureException(String message) {
		super(message);
	}

	public ProcessFailureException(Throwable cause) {
		super(cause);
	}

	public ProcessFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}
