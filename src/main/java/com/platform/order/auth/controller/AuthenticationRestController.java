package com.platform.order.auth.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.auth.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.auth.controller.dto.response.LogoutAuthResponseDto;
import com.platform.order.auth.controller.request.LoginAuthRequestDto;
import com.platform.order.auth.service.AuthService;
import com.platform.order.common.ApiResponse;
import com.platform.order.security.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthenticationRestController {
	private static final String SUCCESS_LOGIN = "success-login";
	private static final String SUCCESS_LOGOUT = "success-logout";

	private final AuthService authService;
	private final LoginSuccessHandler loginSuccessHandler;

	private final LogoutHandler logoutHandler;

	@Operation(summary = "로그인", description = "사용자가 아이디와 패스워드를 가지고 로그인을 한다.")
	@PostMapping("/login")
	public ApiResponse<String> login(
		@Valid @RequestBody LoginAuthRequestDto loginDto,
		HttpServletResponse response
	) {
		LoginAuthResponseDto loginResponse = authService.login(loginDto);
		loginSuccessHandler.onAuthenticationSuccess(response, loginResponse);

		return new ApiResponse<>(SUCCESS_LOGIN);
	}

	@DeleteMapping("/logout")
	public ApiResponse<String> logout(
		@AuthenticationPrincipal JwtAuthentication principal,
		HttpServletResponse response
	) {
		LogoutAuthResponseDto logoutResponse = authService.logout(principal.id());
		logoutHandler.clearToCookie(response, logoutResponse);

		return new ApiResponse<>(SUCCESS_LOGOUT);
	}

}
