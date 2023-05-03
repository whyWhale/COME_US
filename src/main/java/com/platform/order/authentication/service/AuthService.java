package com.platform.order.authentication.service;

import static com.platform.order.common.security.JwtProviderManager.CustomClaim;
import static java.text.MessageFormat.format;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.authentication.controller.dto.request.LoginAuthRequestDto;
import com.platform.order.authentication.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.authentication.controller.dto.response.LogoutAuthResponseDto;
import com.platform.order.authentication.service.mapper.AuthMapper;
import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtProperty;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {
	private final UserRepository userRepository;
	private final JwtProviderManager jwtProviderManager;
	private final PasswordEncoder passwordEncoder;
	private final JwtProperty jwtProperty;
	private final AuthMapper authMapper;

	public LoginAuthResponseDto login(LoginAuthRequestDto loginDto) {
		UserEntity principal = userRepository.findByUsername(loginDto.username())
			.orElseThrow(() -> new BusinessException(
				format("not exist User username: {0}", loginDto.username()),
				ErrorCode.NOT_AUTHENTICATE));

		boolean isMatchCredential = passwordEncoder.matches(loginDto.password(), principal.getPassword());

		if (!isMatchCredential) {
			throw new BusinessException(
				format("not match User encodingPassword: {0}", loginDto.password()),
				ErrorCode.NOT_AUTHENTICATE
			);
		}

		CustomClaim claim = CustomClaim.builder()
			.userId(principal.getId())
			.roles(new String[] {principal.getRole().getKey()})
			.build();

		String accessToken = jwtProviderManager.generateAccessToken(claim);
		String refreshToken = jwtProviderManager.generateRefreshToken(principal.getId());

		return authMapper.toLoginResponse(accessToken, refreshToken, jwtProperty);
	}

	public LogoutAuthResponseDto logout(Long id) {
		jwtProviderManager.removeRefreshToken(id);

		return authMapper.toLogoutResponse(jwtProperty);
	}
}
