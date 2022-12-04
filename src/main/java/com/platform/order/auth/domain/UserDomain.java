package com.platform.order.auth.domain;

import java.text.MessageFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.platform.order.auth.domain.entity.User;
import com.platform.order.auth.domain.repository.UserRepository;
import com.platform.order.auth.usecase.converter.AuthConverter;
import com.platform.order.auth.view.dto.AuthDto;
import com.platform.order.auth.view.dto.UserDto;
import com.platform.order.common.exception.ErrorCode;
import com.platform.order.common.exception.NotFoundResource;

@Service
public class UserDomain {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthConverter authConverter;

	public UserDomain(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthConverter authConverter) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authConverter = authConverter;
	}

	public AuthDto.Response findByUsername(String username) {
		User foundUser = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundResource(
			MessageFormat.format("not found user, username :{0}", username),
			ErrorCode.NOT_FOUND_RESOURCES)
		);

		return authConverter.toUserResponse(foundUser);
	}

	public User save(UserDto.SignUpRequest signUpRequestDto) {
		return userRepository.save(User.builder()
			.username(signUpRequestDto.username())
			.password(passwordEncoder.encode(signUpRequestDto.password()))
			.nickName(signUpRequestDto.nickName())
			.email(signUpRequestDto.email())
			.role(signUpRequestDto.role())
			.build()
		);
	}
}
