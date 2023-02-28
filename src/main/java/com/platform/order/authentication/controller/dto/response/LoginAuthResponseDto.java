package com.platform.order.authentication.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginAuthResponseDto(@JsonIgnore TokenResponseDto accessToken,
								   @JsonIgnore TokenResponseDto refreshToken) {
}
