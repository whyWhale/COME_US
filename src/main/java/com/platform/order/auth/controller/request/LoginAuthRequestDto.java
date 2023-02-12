package com.platform.order.auth.controller.request;

import javax.validation.constraints.NotBlank;

public record LoginAuthRequestDto(@NotBlank
								  String username,
								  @NotBlank
								  String password) {
}
