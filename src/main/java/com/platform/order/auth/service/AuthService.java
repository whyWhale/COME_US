package com.platform.order.auth.service;

import java.text.MessageFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.platform.order.auth.controller.dto.request.LoginAuthRequestDto;
import com.platform.order.auth.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.auth.controller.dto.response.LogoutAuthResponseDto;
import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtConfig;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	private final UserRepository userRepository;
	private final JwtProviderManager jwtProviderManager;
	private final PasswordEncoder passwordEncoder;
	private final JwtConfig jwtConfig;
	private final AuthMapper authMapper;

	public LoginAuthResponseDto login(LoginAuthRequestDto loginDto) {
		UserEntity principal = userRepository.findByUsername(loginDto.username())
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("not exist User username: {0}", loginDto.username()),
				ErrorCode.NOT_AUTHENTICATE)
			);

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

		return authMapper.toLoginResponse(accessToken, refreshToken, jwtConfig);
	}

	public LogoutAuthResponseDto logout(Long id) {
		jwtProviderManager.removeRefreshToken(id);

		return authMapper.toLogoutResponse(jwtConfig);
	}
}
