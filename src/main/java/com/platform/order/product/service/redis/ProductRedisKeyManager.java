package com.platform.order.product.service.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductRedisKeyManager {
	// sortedSet key
	SORTED_SET_WISH("product::wishCount"),
	SORTED_SET_VIEW("product::viewCount"),

	// set key
	SET_VIEW("product::viewCount::");

	private String key;

}
