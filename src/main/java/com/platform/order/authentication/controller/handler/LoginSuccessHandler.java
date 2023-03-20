package com.platform.order.authentication.controller.handler;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.platform.order.authentication.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.authentication.controller.dto.response.TokenResponseDto;
import com.platform.order.common.security.constant.CookieProperty;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LoginSuccessHandler {
	private final CookieProperty cookieProperty;

	public void onLoginSuccess(HttpServletResponse response, LoginAuthResponseDto loginResponse) {
		ResponseCookie accessCookie = createCookie(loginResponse.accessToken());
		ResponseCookie refreshCookie = createCookie(loginResponse.refreshToken());

		response.setHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
	}

	private ResponseCookie createCookie(TokenResponseDto tokenResponse) {
		return ResponseCookie.from(tokenResponse.header(), tokenResponse.token())
			.path("/")
			.httpOnly(true)
			.secure(cookieProperty.secure())
			.domain(cookieProperty.domain())
			.maxAge(tokenResponse.expirySeconds())
			.sameSite(cookieProperty.sameSite().attributeValue())
			.build();
	}
}


