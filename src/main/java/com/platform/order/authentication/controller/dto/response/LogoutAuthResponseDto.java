package com.platform.order.authentication.controller.dto.response;

public record LogoutAuthResponseDto(String accessTokenHeader, String refreshTokenHeader) {
}
