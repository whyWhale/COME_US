package com.platform.order.user.controller.dto.request;

import com.platform.order.user.domain.entity.Role;

public record SignUpUserRequestDto(
	String username,
	String email,
	String password,
	String nickName,
	Role role) {

}
