package com.platform.order.security.exception;

public class TokenNotFoundException extends RuntimeException {
	private TokenNotFoundException() {
	}

	public TokenNotFoundException(String message) {
		super(message);
	}

	public TokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
