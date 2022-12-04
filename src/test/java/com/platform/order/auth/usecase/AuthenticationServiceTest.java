package com.platform.order.auth.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.platform.order.auth.domain.UserDomain;
import com.platform.order.auth.domain.entity.Role;
import com.platform.order.auth.domain.entity.User;
import com.platform.order.auth.usecase.converter.AuthConverter;
import com.platform.order.auth.view.dto.AuthDto;
import com.platform.order.common.exception.BusinessException;
import com.platform.order.common.exception.NotFoundResource;
import com.platform.order.security.JwtProviderManager;
import com.platform.order.security.Token;
import com.platform.order.security.property.JwtConfig;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@InjectMocks
	AuthService authService;

	@Mock
	UserDomain userDomain;
	@Mock
	JwtProviderManager jwtProviderManager;
	@Mock
	PasswordEncoder passwordEncoder;
	@Mock
	AuthConverter authConverter;

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
		AuthDto.LoginRequest loginRequest = new AuthDto.LoginRequest("whyWhale", "whywhale1234!");
		User savedUser = User.builder()
			.id(1L)
			.username(loginRequest.username())
			.password(loginRequest.password())
			.role(Role.USER)
			.build();

		AuthDto.Response userResponse = new AuthDto.Response(
			savedUser.getId(),
			savedUser.getUsername(),
			savedUser.getRole(),
			loginRequest.password()
		);

		JwtProviderManager.CustomClaim claim = JwtProviderManager.CustomClaim.builder()
			.userId(savedUser.getId())
			.roles(new String[] {savedUser.getRole().getKey()})
			.build();

		String expectedToken = claim.toString();

		AuthDto.LoginResponse expectedResponse = new AuthDto.LoginResponse(
			new AuthDto.TokenResponse(
				concreteJwtConfig.accessToken().header(),
				expectedToken,
				concreteJwtConfig.accessToken().expirySeconds()
			),
			new AuthDto.TokenResponse(
				concreteJwtConfig.refreshToken().header(),
				expectedToken,
				concreteJwtConfig.refreshToken().expirySeconds()
			)
		);

		given(userDomain.findByUsername(loginRequest.username())).willReturn(userResponse);
		given(passwordEncoder.matches(loginRequest.password(), savedUser.getPassword())).willReturn(true);
		given(jwtProviderManager.generateAccessToken(claim)).willReturn(expectedToken);
		given(jwtProviderManager.generateRefreshToken(savedUser.getId())).willReturn(expectedToken);
		given(
			authConverter.toLoginResponse(expectedToken, expectedToken, concreteJwtConfig)
		).willReturn(expectedResponse);

		//when
		AuthDto.LoginResponse loginResponse = authService.login(loginRequest);

		//then
		verify(userDomain, times(1)).findByUsername(any(String.class));
		verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
		verify(jwtProviderManager, times(1)).generateAccessToken(any(JwtProviderManager.CustomClaim.class));
		verify(jwtProviderManager, times(1)).generateRefreshToken(any(Long.class));

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
			AuthDto.LoginRequest requestDto = new AuthDto.LoginRequest(notExistUsername, "password");

			given(userDomain.findByUsername(requestDto.username())).willThrow(NotFoundResource.class);
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
			AuthDto.LoginRequest requestDto = new AuthDto.LoginRequest(notExistUsername, "password");
			AuthDto.Response foundUser = new AuthDto.Response(
				1L,
				requestDto.username(),
				Role.USER,
				"asdjk039-+0d9a");

			given(userDomain.findByUsername(requestDto.username())).willReturn(foundUser);
			given(passwordEncoder.matches(requestDto.password(), foundUser.encodingPassword())).willReturn(false);

			//when
			//then
			Assertions.assertThatThrownBy(() -> {
				authService.login(requestDto);
			}).isInstanceOf(BusinessException.class);
		}
	}
}