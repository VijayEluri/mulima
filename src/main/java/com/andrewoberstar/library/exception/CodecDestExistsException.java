package com.andrewoberstar.library.exception;

public class CodecDestExistsException extends KnownCodecException {
	private static final long serialVersionUID = 1L;

	public CodecDestExistsException() {
		super();
	}

	public CodecDestExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodecDestExistsException(String message) {
		super(message);
	}

	public CodecDestExistsException(Throwable cause) {
		super(cause);
	}

}
