package com.platform.order.authentication.service;

import org.springframework.stereotype.Component;

import com.platform.order.authentication.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.authentication.controller.dto.response.LogoutAuthResponseDto;
import com.platform.order.authentication.controller.dto.response.TokenResponseDto;
import com.platform.order.common.security.constant.JwtConfig;

@Component
public class AuthMapper {

	public LoginAuthResponseDto toLoginResponse(String accessToken, String refreshToken, JwtConfig jwtConfig) {
		return new LoginAuthResponseDto(
			new TokenResponseDto(jwtConfig.accessToken().header(),
				accessToken,
				jwtConfig.accessToken().expirySeconds()),
			new TokenResponseDto(
				jwtConfig.refreshToken().header(),
				refreshToken,
				jwtConfig.refreshToken().expirySeconds())
		);
	}

	public LogoutAuthResponseDto toLogoutResponse(JwtConfig jwtConfig) {
		return new LogoutAuthResponseDto(jwtConfig.accessToken().header(), jwtConfig.refreshToken().header());
	}
}
