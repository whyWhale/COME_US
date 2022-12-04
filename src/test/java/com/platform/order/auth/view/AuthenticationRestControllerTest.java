package com.platform.order.auth.view;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.order.auth.domain.entity.Role;
import com.platform.order.auth.domain.entity.User;
import com.platform.order.auth.usecase.AuthService;
import com.platform.order.auth.view.dto.AuthDto;
import com.platform.order.common.ApiResponse;
import com.platform.order.security.JwtProviderManager;
import com.platform.order.security.TokenService;
import com.platform.order.security.WebSecurityConfig;
import com.platform.order.security.WithJwtMockUser;
import com.platform.order.security.property.JwtConfig;

@WebMvcTest({AuthenticationRestController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	LoginSuccessHandler.class,
	LogoutHandler.class,
	LogoutHandler.class,
	JwtConfig.class})
class AuthenticationRestControllerTest {

	static final String URI_PREFIX = "/api/users";
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	JwtConfig jwtConfig;

	@Autowired
	LoginSuccessHandler loginSuccessHandler;

	@Autowired
	LogoutHandler logoutHandler;

	@MockBean
	AuthService authService;

	@MockBean
	TokenService tokenService;

	@Test
	@DisplayName("로그인에 성공한다.")
	void
	testLogin() throws Exception {
		//given
		User loginUser = User.builder()
			.id(1L)
			.username("whyWhale")
			.password("wls23333")
			.role(Role.OWNER)
			.build();

		AuthDto.LoginRequest requestDto = new AuthDto.LoginRequest(
			loginUser.getUsername(), loginUser.getPassword()
		);
		String requestBody = objectMapper.writeValueAsString(requestDto);

		String expectedResponse = objectMapper.writeValueAsString(new ApiResponse<>("success-login"));
		AuthDto.LoginRequest loginDto = new AuthDto.LoginRequest(loginUser.getUsername(), loginUser.getPassword());
		AuthDto.TokenResponse accessTokenResponse = new AuthDto.TokenResponse("access-token", "access-token", 1000);
		AuthDto.TokenResponse refreshTokenResponse = new AuthDto.TokenResponse("refresh-token", "refresh-token", 60000);
		AuthDto.LoginResponse loginResponse = new AuthDto.LoginResponse(accessTokenResponse, refreshTokenResponse);

		BDDMockito.given(authService.login(loginDto)).willReturn(loginResponse);

		//when
		ResultActions perform = mockMvc.perform(
			post(URI_PREFIX + "/login")
				.content(requestBody)
				.contentType(APPLICATION_JSON)
		);

		//then
		verify(authService, times(1)).login(requestDto);

		MvcResult result = perform.andExpect(status().isOk())
			.andExpect(cookie().exists(loginResponse.accessToken().header()))
			.andExpect(cookie()
				.maxAge(
					loginResponse.accessToken().header(), (int)accessTokenResponse.expirySeconds()
				)
			)
			.andExpect(cookie().exists(loginResponse.refreshToken().header()))
			.andExpect(cookie()
				.maxAge(
					loginResponse.refreshToken().header(), (int)refreshTokenResponse.expirySeconds()
				)
			)
			.andReturn();

		String response = result.getResponse().getContentAsString();

		Assertions.assertThat(response).isEqualTo(expectedResponse);
	}

	@Test
	@WithJwtMockUser
	@DisplayName("인증된 사용자가 로그아웃 한다.")
	void testLogout() throws Exception {
		//given
		AuthDto.LogoutResponse logoutResponse = new AuthDto.LogoutResponse(
			jwtConfig.accessToken().header(),
			jwtConfig.refreshToken().header()
		);
		BDDMockito.given(authService.logout(any())).willReturn(logoutResponse);
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

		BDDMockito.verify(authService, times(1)).logout(any());
	}

	@DisplayName("아이디 또는 비밀번호가 유효하지 않다면 실패한다.")
	@ParameterizedTest(name = "{index}: username: {0} | encodingPassword: {1}")
	@CsvSource(value = {
		"'',paswword",
		"'username',''",
		"'username','      '",
		"'       ','encodingPassword'"
	})
	void testFailNotProperArguments(String username, String password) throws Exception {
		//given
		AuthDto.LoginRequest loginRequest = new AuthDto.LoginRequest(username, password);
		String requestBody = objectMapper.writeValueAsString(loginRequest);

		//when
		ResultActions perform = mockMvc.perform(
			post(URI_PREFIX + "/login")
				.content(requestBody)
				.contentType(APPLICATION_JSON)
		);

		//then
		perform.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("인증되지 않는 사용자가 로그아웃을 하면 실패한다.")
	void testLogoutWithNotAuthenticationUser() throws Exception {
		//given

		//when
		ResultActions perform = mockMvc.perform(
			delete(URI_PREFIX + "/logout")
		);
		//then
		perform.andExpect(status().isUnauthorized());
	}

}