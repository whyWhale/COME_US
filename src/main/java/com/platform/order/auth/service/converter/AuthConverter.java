package com.platform.order.auth.service.converter;

import org.springframework.stereotype.Component;

import com.platform.order.auth.controller.dto.response.AuthResponse;
import com.platform.order.auth.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.auth.controller.dto.response.LogoutAuthResponseDto;
import com.platform.order.auth.controller.dto.response.TokenResponseDto;
import com.platform.order.security.property.JwtConfig;
import com.platform.order.user.domain.entity.UserEntity;

@Component
public class AuthConverter {

	public AuthResponse toUserResponse(UserEntity userEntity) {
		return new AuthResponse(
			userEntity.getId(),
			userEntity.getUsername(),
			userEntity.getRole(),
			userEntity.getPassword()
		);
	}

	public LoginAuthResponseDto toLoginResponse(String accessToken, String refreshToken, JwtConfig jwtConfig) {
		return new LoginAuthResponseDto(
			new TokenResponseDto(
				jwtConfig.accessToken().header(),
				accessToken,
				jwtConfig.accessToken().expirySeconds()
			),
			new TokenResponseDto(
				jwtConfig.refreshToken().header(),
				refreshToken,
				jwtConfig.refreshToken().expirySeconds()
			)
		);
	}

	public LogoutAuthResponseDto toLogoutResponse(JwtConfig jwtConfig) {
		return new LogoutAuthResponseDto(
			jwtConfig.accessToken().header(),
			jwtConfig.refreshToken().header()
		);
	}
}
