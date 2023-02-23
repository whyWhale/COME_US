package com.platform.order.common.security.exception;

public class JwtAccessTokenNotFoundException extends TokenNotFoundException {

	public JwtAccessTokenNotFoundException(String message) {
		super(message);
	}

	public JwtAccessTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
