package com.platform.order.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.platform.order.auth.controller.dto.request.LoginAuthRequestDto;
import com.platform.order.auth.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.auth.controller.dto.response.TokenResponseDto;
import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.custom.NotFoundResource;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtConfig;
import com.platform.order.common.security.model.Token;
import com.platform.order.testenv.ServiceTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

class AuthenticationServiceTest extends ServiceTest {

	@InjectMocks
	AuthService authService;

	@Mock
	UserRepository userRepository;

	@Mock
	JwtProviderManager jwtProviderManager;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	AuthMapper authMapper;

	JwtConfig concreteJwtConfig;

	@BeforeEach
	void setJwtConfig() {
		concreteJwtConfig = new JwtConfig(
			new Token("access-token", 1000),
			new Token("refresh-token", 60000),
			"order-renewal",
			"secret-key");

		ReflectionTestUtils.setField(authService, "jwtConfig", concreteJwtConfig);
	}

	@Test
	@DisplayName("등록된 사용자가 로그인에 성공한다.")
	void testLogin() {
		//given
		LoginAuthRequestDto loginRequest = new LoginAuthRequestDto("whyWhale", "whywhale1234!");
		UserEntity savedUserEntity = UserEntity.builder()
			.username(loginRequest.username())
			.password(loginRequest.password())
			.role(Role.USER)
			.build();

		UserEntity user = UserEntity.builder()
			.username(savedUserEntity.getUsername())
			.role(savedUserEntity.getRole())
			.password(loginRequest.password())
			.build();

		JwtProviderManager.CustomClaim claim = JwtProviderManager.CustomClaim.builder()
			.userId(savedUserEntity.getId())
			.roles(new String[] {savedUserEntity.getRole().getKey()})
			.build();

		String expectedToken = claim.toString();

		LoginAuthResponseDto expectedResponse = new LoginAuthResponseDto(
			new TokenResponseDto(
				concreteJwtConfig.accessToken().header(),
				expectedToken,
				concreteJwtConfig.accessToken().expirySeconds()
			),
			new TokenResponseDto(
				concreteJwtConfig.refreshToken().header(),
				expectedToken,
				concreteJwtConfig.refreshToken().expirySeconds()
			)
		);

		given(userRepository.findByUsername(loginRequest.username())).willReturn(Optional.of(user));
		given(passwordEncoder.matches(loginRequest.password(), savedUserEntity.getPassword())).willReturn(true);
		given(jwtProviderManager.generateAccessToken(claim)).willReturn(expectedToken);
		given(jwtProviderManager.generateRefreshToken(savedUserEntity.getId())).willReturn(expectedToken);
		given(
			authMapper.toLoginResponse(expectedToken, expectedToken, concreteJwtConfig)
		).willReturn(expectedResponse);

		//when
		LoginAuthResponseDto loginResponse = authService.login(loginRequest);

		//then
		verify(userRepository, times(1)).findByUsername(any(String.class));
		verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
		verify(jwtProviderManager, times(1)).generateAccessToken(any(JwtProviderManager.CustomClaim.class));

		Assertions.assertThat(loginResponse.accessToken().token()).isEqualTo(expectedToken);
		Assertions.assertThat(loginResponse.refreshToken().token()).isEqualTo(expectedToken);
	}

	@DisplayName("로그인 시,")
	@Nested
	class LoginUseCaseViolation {
		@Test
		@DisplayName("존재하지 않는 username 이면 실패한다.")
		void testFailNotExistUsername() {
			//given
			String notExistUsername = "kora";
			LoginAuthRequestDto requestDto = new LoginAuthRequestDto(notExistUsername, "password");

			given(userRepository.findByUsername(requestDto.username())).willThrow(NotFoundResource.class);
			//when
			//then
			Assertions.assertThatThrownBy(() -> {
				authService.login(requestDto);
			}).isInstanceOf(BusinessException.class);
		}

		@Test
		@DisplayName("비밀번호가 일치하지 않으면 실패한다.")
		void testFailNotMatchPassword() {
			//given
			String notExistUsername = "kora";
			LoginAuthRequestDto requestDto = new LoginAuthRequestDto(notExistUsername, "password");
			UserEntity foundUser = UserEntity
				.builder()
				.username(requestDto.username())
				.password("amksd1ju23iu12")
				.build();

			given(userRepository.findByUsername(requestDto.username())).willReturn(Optional.of(foundUser));
			given(passwordEncoder.matches(requestDto.password(), foundUser.getPassword())).willReturn(false);

			//when
			//then
			Assertions.assertThatThrownBy(() -> {
				authService.login(requestDto);
			}).isInstanceOf(BusinessException.class);
		}
	}
}