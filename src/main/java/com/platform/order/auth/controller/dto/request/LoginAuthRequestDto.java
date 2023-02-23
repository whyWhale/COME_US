package com.platform.order.auth.controller.dto.request;

import javax.validation.constraints.NotBlank;

public record LoginAuthRequestDto(@NotBlank String username, @NotBlank String password) {
}
