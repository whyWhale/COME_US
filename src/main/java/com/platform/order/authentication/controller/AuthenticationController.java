package com.platform.order.authentication.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.authentication.controller.dto.request.LoginAuthRequestDto;
import com.platform.order.authentication.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.authentication.controller.dto.response.LogoutAuthResponseDto;
import com.platform.order.authentication.controller.handler.LoginSuccessHandler;
import com.platform.order.authentication.controller.handler.LogoutSuccessHandler;
import com.platform.order.authentication.service.AuthService;
import com.platform.order.common.security.model.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
	private final AuthService authService;
	private final LoginSuccessHandler loginSuccessHandler;
	private final LogoutSuccessHandler logoutSuccessHandler;

	@Operation(summary = "로그인", description = "사용자가 아이디와 패스워드를 가지고 로그인을 한다.")
	@PostMapping("/login")
	public void login(
		@Valid
		@RequestBody LoginAuthRequestDto loginDto,

		HttpServletResponse response
	) {
		LoginAuthResponseDto loginResponse = authService.login(loginDto);
		loginSuccessHandler.onLoginSuccess(response, loginResponse);
	}

	@DeleteMapping("/logout")
	public void logout(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		HttpServletResponse response
	) {
		LogoutAuthResponseDto logoutResponse = authService.logout(principal.id());
		logoutSuccessHandler.onLogoutSuccess(response, logoutResponse);
	}

}
