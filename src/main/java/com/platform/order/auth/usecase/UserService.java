package com.platform.order.auth.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.auth.domain.UserDomain;
import com.platform.order.auth.domain.entity.User;
import com.platform.order.auth.usecase.converter.AuthConverter;
import com.platform.order.auth.view.dto.UserDto;

@Transactional(readOnly = true)
@Service
public class UserService {

	private final UserDomain userDomain;
	private final AuthConverter authConverter;

	public UserService(UserDomain userDomain, AuthConverter authConverter) {
		this.userDomain = userDomain;
		this.authConverter = authConverter;
	}

	public void save(UserDto.SignUpRequest signUpRequestDto) {
		userDomain.save(signUpRequestDto);
	}

}
