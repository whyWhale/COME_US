package com.platform.order.auth.usecase;

import java.text.MessageFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.platform.order.auth.domain.UserDomain;
import com.platform.order.auth.usecase.converter.AuthConverter;
import com.platform.order.auth.view.dto.AuthDto;
import com.platform.order.common.exception.BusinessException;
import com.platform.order.common.exception.ErrorCode;
import com.platform.order.common.exception.NotFoundResource;
import com.platform.order.security.JwtProviderManager;
import com.platform.order.security.property.JwtConfig;

@Service
public class AuthService {
	private final JwtProviderManager jwtProviderManager;
	private final PasswordEncoder passwordEncoder;
	private final JwtConfig jwtConfig;
	private final UserDomain userDomain;

	private final AuthConverter authConverter;

	public AuthService(
		JwtProviderManager jwtProviderManager,
		PasswordEncoder passwordEncoder,
		JwtConfig jwtConfig,
		UserDomain userDomain,
		AuthConverter authConverter) {
		this.jwtProviderManager = jwtProviderManager;
		this.passwordEncoder = passwordEncoder;
		this.jwtConfig = jwtConfig;
		this.userDomain = userDomain;
		this.authConverter = authConverter;
	}

	public AuthDto.LoginResponse login(AuthDto.LoginRequest loginDto) {
		AuthDto.Response principal = null;

		try {
			principal = userDomain.findByUsername(loginDto.username());
		} catch (NotFoundResource notFoundResource) {
			throw new BusinessException(
				MessageFormat.format("not exist User username: {0}", loginDto.username()),
				ErrorCode.NOT_AUTHENTICATE
			);
		}

		boolean isMatchCredential = passwordEncoder.matches(loginDto.password(), principal.encodingPassword());

		if (!isMatchCredential) {
			throw new BusinessException(
				MessageFormat.format("not match User encodingPassword: {0}", loginDto.password()),
				ErrorCode.NOT_AUTHENTICATE
			);
		}

		JwtProviderManager.CustomClaim claim = JwtProviderManager.CustomClaim.builder()
			.userId(principal.userId())
			.roles(new String[] {principal.role().getKey()})
			.build();

		String accessToken = jwtProviderManager.generateAccessToken(claim);
		String refreshToken = jwtProviderManager.generateRefreshToken(principal.userId());

		return authConverter.toLoginResponse(accessToken, refreshToken, jwtConfig);
	}

	public AuthDto.LogoutResponse logout(Long id) {
		jwtProviderManager.removeRefreshToken(id);

		return authConverter.toLogoutResponse(jwtConfig);
	}
}
