package com.andrewoberstar.library.exception;

public abstract class KnownCodecException extends CodecException {
	private static final long serialVersionUID = 1L;
	
	public KnownCodecException() {
		super();
	}

	public KnownCodecException(String message, Throwable cause) {
		super(message, cause);
	}

	public KnownCodecException(String message) {
		super(message);
	}

	public KnownCodecException(Throwable cause) {
		super(cause);
	}

}
