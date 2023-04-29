package com.platform.order.authentication.controller.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginAuthRequestDto(
	@Schema(description = "회원 아이디", required = true)
	@NotBlank
	String username,
	@Schema(description = "비밀번호", required = true)
	@NotBlank
	String password
) {
}
