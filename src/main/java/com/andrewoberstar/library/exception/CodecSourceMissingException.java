package com.andrewoberstar.library.exception;

public class CodecSourceMissingException extends KnownCodecException {
	private static final long serialVersionUID = 1L;

	public CodecSourceMissingException() {
		super();
	}

	public CodecSourceMissingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodecSourceMissingException(String message) {
		super(message);
	}

	public CodecSourceMissingException(Throwable cause) {
		super(cause);
	}

}
