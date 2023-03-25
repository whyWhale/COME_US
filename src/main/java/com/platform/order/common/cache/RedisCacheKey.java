package com.platform.order.common.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisCacheKey {
	CATEGORIES("categories");

	private String cacheKey;
}
