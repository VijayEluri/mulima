package com.andrewoberstar.library.exception;

public class FatalIOException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FatalIOException() {
		super();
	}

	public FatalIOException(String message) {
		super(message);
	}

	public FatalIOException(Throwable cause) {
		super(cause);
	}

	public FatalIOException(String message, Throwable cause) {
		super(message, cause);
	}

}
