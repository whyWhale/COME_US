package com.platform.order.auth.view;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.platform.order.auth.view.dto.AuthDto;
import com.platform.order.security.property.CookieProperty;

@Component
public class LogoutHandler {
	private final CookieProperty cookieProperty;

	public LogoutHandler(CookieProperty cookieProperty) {
		this.cookieProperty = cookieProperty;
	}

	public void clearToCookie(HttpServletResponse response, AuthDto.LogoutResponse logoutResponse) {
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
