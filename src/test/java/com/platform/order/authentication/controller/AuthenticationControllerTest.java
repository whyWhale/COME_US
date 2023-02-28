package com.platform.order.authentication.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.order.authentication.controller.dto.request.LoginAuthRequestDto;
import com.platform.order.authentication.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.authentication.controller.dto.response.LogoutAuthResponseDto;
import com.platform.order.authentication.controller.dto.response.TokenResponseDto;
import com.platform.order.authentication.controller.handler.LoginSuccessHandler;
import com.platform.order.authentication.controller.handler.LogoutSuccessHandler;
import com.platform.order.authentication.service.AuthService;
import com.platform.order.common.config.WebSecurityConfig;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtConfig;
import com.platform.order.common.security.service.TokenService;
import com.platform.order.security.WithJwtMockUser;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;

@WebMvcTest({AuthenticationController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	LoginSuccessHandler.class,
	LogoutSuccessHandler.class,
	LogoutSuccessHandler.class,
	JwtConfig.class})
class AuthenticationControllerTest {

	static final String URI_PREFIX = "/api/auth";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	JwtConfig jwtConfig;

	@Autowired
	LoginSuccessHandler loginSuccessHandler;

	@Autowired
	LogoutSuccessHandler logoutSuccessHandler;

	@MockBean
	AuthService authService;

	@MockBean
	TokenService tokenService;

	@Test
	@DisplayName("로그인에 성공한다.")
	void testLogin() throws Exception {
		//given
		UserEntity loginUserEntity = UserEntity.builder()
			.username("whyWhale")
			.password("wls23333")
			.role(Role.USER)
			.build();
		var requestDto = new LoginAuthRequestDto(loginUserEntity.getUsername(), loginUserEntity.getPassword());
		var loginDto = new LoginAuthRequestDto(loginUserEntity.getUsername(), loginUserEntity.getPassword());
		var accessTokenResponse = new TokenResponseDto("access-token", "access-token", 1000);
		var refreshTokenResponse = new TokenResponseDto("refresh-token", "refresh-token", 60000);
		var loginResponse = new LoginAuthResponseDto(accessTokenResponse, refreshTokenResponse);

		given(authService.login(loginDto)).willReturn(loginResponse);
		//when
		ResultActions perform = mockMvc.perform(
			post(URI_PREFIX + "/login")
				.content(objectMapper.writeValueAsString(requestDto))
				.contentType(APPLICATION_JSON)
		);

		//then
		verify(authService, times(1)).login(requestDto);
		perform.andExpect(status().isOk())
			.andExpect(cookie().exists(loginResponse.accessToken().header()))
			.andExpect(cookie()
				.maxAge(loginResponse.accessToken().header(), (int)accessTokenResponse.expirySeconds())
			).andExpect(cookie().exists(loginResponse.refreshToken().header()))
			.andExpect(cookie()
				.maxAge(loginResponse.refreshToken().header(), (int)refreshTokenResponse.expirySeconds())
			).andReturn();
	}

	@Test
	@WithJwtMockUser
	@DisplayName("인증된 사용자가 로그아웃 한다.")
	void testLogout() throws Exception {
		//given
		var logoutResponse = new LogoutAuthResponseDto(
			jwtConfig.accessToken().header(),
			jwtConfig.refreshToken().header());

		given(authService.logout(any())).willReturn(logoutResponse);
		//when
		ResultActions perform = mockMvc.perform(
			delete(URI_PREFIX + "/logout")
		);
		//then
		perform.andExpect(status().isOk())
			.andExpect(cookie().exists(jwtConfig.accessToken().header()))
			.andExpect(cookie().maxAge(jwtConfig.accessToken().header(), 0))
			.andExpect(cookie().exists(jwtConfig.refreshToken().header()))
			.andExpect(cookie().maxAge(jwtConfig.refreshToken().header(), 0));
		verify(authService, times(1)).logout(any());
	}

	@DisplayName("아이디 또는 비밀번호가 유효하지 않다면 실패한다.")
	@ParameterizedTest(name = "{index}: username: {0} | encodingPassword: {1}")
	@CsvSource(value = {
		"'',paswword",
		"'username',''",
		"'username','      '",
		"'       ','encodingPassword'"})
	void failNotProperArguments(String username, String password) throws Exception {
		//given
		LoginAuthRequestDto loginRequest = new LoginAuthRequestDto(username, password);
		String requestBody = objectMapper.writeValueAsString(loginRequest);
		//when
		ResultActions perform = mockMvc.perform(
			post(URI_PREFIX + "/login")
				.content(requestBody)
				.contentType(APPLICATION_JSON));
		//then
		perform.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("인증되지 않는 사용자가 로그아웃을 하면 실패한다.")
	void failLogoutWithNotAuthenticationUser() throws Exception {
		//given
		//when
		ResultActions perform = mockMvc.perform(delete(URI_PREFIX + "/logout"));
		//then
		perform.andExpect(status().isUnauthorized());
	}

}