package com.platform.order.auth.usecase;

import java.text.MessageFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;
import com.platform.order.auth.usecase.converter.AuthConverter;
import com.platform.order.auth.view.dto.AuthDto;
import com.platform.order.common.exception.BusinessException;
import com.platform.order.common.exception.ErrorCode;
import com.platform.order.common.exception.NotFoundResource;
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

	public AuthDto.LoginResponse login(AuthDto.LoginRequest loginDto) {
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

	public AuthDto.LogoutResponse logout(Long id) {
		jwtProviderManager.removeRefreshToken(id);

		return authConverter.toLogoutResponse(jwtConfig);
	}
}
