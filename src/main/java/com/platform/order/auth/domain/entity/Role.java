package com.platform.order.auth.domain.entity;

public enum Role {
	USER("ROLE_USER", "사용자"),
	OWNER("ROLE_OWNER", "가맹주"),
	ADMIN("ROLE_ADMIN", "관리자");

	private final String key;
	private final String position;

	Role(String key, String position) {
		this.key = key;
		this.position = position;
	}

	public String getKey() {
		return key;
	}

	public String getPosition() {
		return position;
	}
}
