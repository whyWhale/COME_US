package com.platform.order.auth.view.dto;

import com.platform.order.auth.domain.entity.Role;

import lombok.Builder;

public class UserDto {
	public record SignUpRequest(
		String username,
		String password,
		String nickName,
		String email,
		Role role
	) {

	}

}
