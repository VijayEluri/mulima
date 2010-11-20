package com.andrewoberstar.library.exception;

public class CodecFailureException extends KnownCodecException {
	private static final long serialVersionUID = 1L;

	public CodecFailureException() {
		super();
	}

	public CodecFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodecFailureException(String message) {
		super(message);
	}

	public CodecFailureException(Throwable cause) {
		super(cause);
	}

}
