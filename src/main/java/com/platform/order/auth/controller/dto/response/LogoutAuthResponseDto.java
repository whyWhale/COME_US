package com.platform.order.auth.controller.dto.response;

public record LogoutAuthResponseDto(String accessTokenHeader,
									String refreshTokenHeader) {
}
