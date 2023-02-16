package com.platform.order.auth.service;

import java.text.MessageFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.platform.order.auth.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.auth.controller.dto.response.LogoutAuthResponseDto;
import com.platform.order.auth.controller.dto.request.LoginAuthRequestDto;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;
import com.platform.order.auth.service.converter.AuthConverter;
import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.custom.ErrorCode;
import com.platform.order.common.exception.custom.NotFoundResource;
import com.platform.order.security.JwtProviderManager;
import com.platform.order.security.property.JwtConfig;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	private final UserRepository userRepository;
	private final JwtProviderManager jwtProviderManager;
	private final PasswordEncoder passwordEncoder;
	private final JwtConfig jwtConfig;
	private final AuthConverter authConverter;

	public LoginAuthResponseDto login(LoginAuthRequestDto loginDto) {
		UserEntity principal = null;

		try {
			principal = userRepository.findByUsername(loginDto.username()).orElseThrow(() -> new NotFoundResource(
				MessageFormat.format("not found user, username :{0}", loginDto.username()),
				ErrorCode.NOT_FOUND_RESOURCES)
			);
		} catch (NotFoundResource notFoundResource) {
			throw new BusinessException(
				MessageFormat.format("not exist User username: {0}", loginDto.username()),
				ErrorCode.NOT_AUTHENTICATE
			);
		}

		boolean isMatchCredential = passwordEncoder.matches(loginDto.password(), principal.getPassword());

		if (!isMatchCredential) {
			throw new BusinessException(
				MessageFormat.format("not match User encodingPassword: {0}", loginDto.password()),
				ErrorCode.NOT_AUTHENTICATE
			);
		}

		JwtProviderManager.CustomClaim claim = JwtProviderManager.CustomClaim.builder()
			.userId(principal.getId())
			.roles(new String[] {principal.getRole().getKey()})
			.build();

		String accessToken = jwtProviderManager.generateAccessToken(claim);
		String refreshToken = jwtProviderManager.generateRefreshToken(principal.getId());

		return authConverter.toLoginResponse(accessToken, refreshToken, jwtConfig);
	}

	public LogoutAuthResponseDto logout(Long id) {
		jwtProviderManager.removeRefreshToken(id);

		return authConverter.toLogoutResponse(jwtConfig);
	}
}
