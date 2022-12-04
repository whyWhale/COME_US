package com.platform.order.security.exception;

public class JwtAccessTokenNotFoundException extends TokenNotFoundException {

	public JwtAccessTokenNotFoundException(String message) {
		super(message);
	}

	public JwtAccessTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
