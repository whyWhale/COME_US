package com.platform.order.authentication.controller.handler;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.platform.order.authentication.controller.dto.response.LogoutAuthResponseDto;
import com.platform.order.common.security.constant.CookieProperty;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LogoutSuccessHandler {
	private final CookieProperty cookieProperty;

	public void clearToCookie(HttpServletResponse response, LogoutAuthResponseDto logoutResponse) {
		Assert.notNull(logoutResponse.accessTokenHeader(), "access token header is cannot be null");
		Assert.notNull(logoutResponse.accessTokenHeader(), "refresh token header is cannot be null");

		ResponseCookie accessTokenCookie = createCookie(logoutResponse.accessTokenHeader());
		ResponseCookie refreshTokenCookie = createCookie(logoutResponse.refreshTokenHeader());

		response.setHeader(SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(SET_COOKIE, refreshTokenCookie.toString());
	}

	private ResponseCookie createCookie(String header) {
		return ResponseCookie.from(header, "")
			.path("/")
			.httpOnly(true)
			.maxAge(0)
			.secure(cookieProperty.secure())
			.domain(cookieProperty.domain())
			.build();
	}
}
