package com.platform.order.authentication.controller.dto.response;

public record TokenResponseDto(
	String header,
	String token,
	long expirySeconds
) {
}
