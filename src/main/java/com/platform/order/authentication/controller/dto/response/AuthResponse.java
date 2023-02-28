package com.platform.order.authentication.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.platform.order.user.domain.entity.Role;

public record AuthResponse(Long userId, String username, Role role, @JsonIgnore String encodingPassword) {
}
