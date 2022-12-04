package com.platform.order.auth.view;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.auth.usecase.AuthService;
import com.platform.order.auth.view.dto.AuthDto;
import com.platform.order.common.ApiResponse;
import com.platform.order.security.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/api/users")
@RestController
public class AuthenticationRestController {
	private static final String SUCCESS_LOGIN = "success-login";
	private static final String SUCCESS_LOGOUT = "success-logout";

	private final AuthService authService;
	private final LoginSuccessHandler loginSuccessHandler;

	private final LogoutHandler logoutHandler;

	public AuthenticationRestController(AuthService authService, LoginSuccessHandler loginSuccessHandler,
		LogoutHandler logoutHandler) {
		this.authService = authService;
		this.loginSuccessHandler = loginSuccessHandler;
		this.logoutHandler = logoutHandler;
	}

	@Operation(summary = "로그인", description = "사용자가 아이디와 패스워드를 가지고 로그인을 한다.")
	@PostMapping("/login")
	public ApiResponse<String> login(
		@Valid @RequestBody AuthDto.LoginRequest loginDto,
		HttpServletResponse response
	) {
		AuthDto.LoginResponse loginResponse = authService.login(loginDto);
		loginSuccessHandler.onAuthenticationSuccess(response, loginResponse);

		return new ApiResponse<>(SUCCESS_LOGIN);
	}

	@DeleteMapping("/logout")
	public ApiResponse<String> logout(
		@AuthenticationPrincipal JwtAuthentication principal,
		HttpServletResponse response
	) {
		AuthDto.LogoutResponse logoutResponse = authService.logout(principal.id());
		logoutHandler.clearToCookie(response, logoutResponse);

		return new ApiResponse<>(SUCCESS_LOGOUT);
	}

}
