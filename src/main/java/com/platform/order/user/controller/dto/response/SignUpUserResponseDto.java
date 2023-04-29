package com.platform.order.user.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpUserResponseDto(
	@Schema(description = "DB 아이디")
	Long userId,

	@Schema(description = "아이디")
	String username,

	@Schema(description = "이메일")
	String email,

	@Schema(description = "닉네임")
	String nickName
) {
}
