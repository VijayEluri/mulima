package com.andrewoberstar.library.exception;

public class UnknownCodecException extends CodecException {
	private static final long serialVersionUID = 1L;

	public UnknownCodecException() {
		super();
	}

	public UnknownCodecException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownCodecException(String message) {
		super(message);
	}

	public UnknownCodecException(Throwable cause) {
		super(cause);
	}

}
