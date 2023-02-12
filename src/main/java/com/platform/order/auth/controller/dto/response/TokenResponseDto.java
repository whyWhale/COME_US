package com.platform.order.auth.controller.dto.response;

public record TokenResponseDto(String header, String token, long expirySeconds) {
}
