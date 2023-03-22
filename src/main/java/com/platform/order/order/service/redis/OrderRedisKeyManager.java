package com.platform.order.order.service.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderRedisKeyManager {
	SORTED_SET_ORDER_PRODUCT("order::product");

	private String key;
}
