package com.platform.order.auth.view.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.platform.order.auth.domain.entity.Role;

public class AuthDto {
	public record Response(
		Long userId,
		String username,
		Role role,
		@JsonIgnore
		String encodingPassword) {

	}

	public record LoginRequest(
		@NotBlank
		String username,
		@NotBlank
		String password) {
	}

	public record LoginResponse(
		@JsonIgnore
		TokenResponse accessToken,

		@JsonIgnore
		TokenResponse refreshToken

	) {
	}

	public record LogoutResponse(
		String accessTokenHeader,
		String refreshTokenHeader
	) {
	}

	public record TokenResponse(
		String header,
		String token,
		long expirySeconds
	) {
	}
}