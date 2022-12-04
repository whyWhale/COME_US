package com.platform.order.auth.usecase.converter;

import org.springframework.stereotype.Component;

import com.platform.order.auth.domain.entity.User;
import com.platform.order.auth.view.dto.AuthDto;
import com.platform.order.security.property.JwtConfig;

@Component
public class AuthConverter {

	public AuthDto.Response toUserResponse(User user) {
		return new AuthDto.Response(
			user.getId(),
			user.getUsername(),
			user.getRole(),
			user.getPassword()
		);
	}

	public AuthDto.LoginResponse toLoginResponse(String accessToken, String refreshToken, JwtConfig jwtConfig) {
		return new AuthDto.LoginResponse(
			new AuthDto.TokenResponse(
				jwtConfig.accessToken().header(),
				accessToken,
				jwtConfig.accessToken().expirySeconds()
			),
			new AuthDto.TokenResponse(
				jwtConfig.refreshToken().header(),
				refreshToken,
				jwtConfig.refreshToken().expirySeconds()
			)
		);
	}

	public AuthDto.LogoutResponse toLogoutResponse(JwtConfig jwtConfig) {
		return new AuthDto.LogoutResponse(
			jwtConfig.accessToken().header(),
			jwtConfig.refreshToken().header()
		);
	}
}
