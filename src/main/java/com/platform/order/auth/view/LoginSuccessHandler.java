package com.platform.order.auth.view;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.platform.order.auth.view.dto.AuthDto;
import com.platform.order.security.property.CookieProperty;

@Component
public class LoginSuccessHandler {

	private final CookieProperty cookieProperty;

	public LoginSuccessHandler(CookieProperty cookieProperty) {
		this.cookieProperty = cookieProperty;
	}

	public void onAuthenticationSuccess(
		HttpServletResponse response,
		AuthDto.LoginResponse loginResponse
	) {
		ResponseCookie accessCookie = createCookie(loginResponse.accessToken());
		ResponseCookie refreshCookie = createCookie(loginResponse.refreshToken());

		response.setHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
	}

	private ResponseCookie createCookie(AuthDto.TokenResponse tokenResponse) {
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


