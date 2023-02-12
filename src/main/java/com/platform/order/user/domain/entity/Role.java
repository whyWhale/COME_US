package com.platform.order.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
	USER("ROLE_USER", "사용자"),
	OWNER("ROLE_OWNER", "가맹주"),
	ADMIN("ROLE_ADMIN", "관리자");

	private final String key;
	private final String position;
}
