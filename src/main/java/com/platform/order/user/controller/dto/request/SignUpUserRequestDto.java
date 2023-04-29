package com.platform.order.user.controller.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.platform.order.user.domain.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpUserRequestDto(
	@Schema(description = "아아디")
	@NotBlank
	String username,

	@Schema(description = "이메일")
	@NotBlank
	@Email
	String email,

	@Schema(description = "비밀번호")
	@NotBlank
	String password,

	@Schema(description = "닉네임")
	@NotBlank
	String nickName,

	@Schema(description = "권한")
	@NotBlank
	Role role
) {

}
