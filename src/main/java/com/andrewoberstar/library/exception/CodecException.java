package com.andrewoberstar.library.exception;

public abstract class CodecException extends Exception {
	private static final long serialVersionUID = 1L;

	public CodecException() {
		super();
	}

	public CodecException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodecException(String message) {
		super(message);
	}

	public CodecException(Throwable cause) {
		super(cause);
	}
}
