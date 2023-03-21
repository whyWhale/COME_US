package com.platform.order.product.service.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductRedisKeyManager {
	// sortedSet key
	SORTED_SET_WISH("ss_product::wishCount"),
	SORTED_SET_VIEW("ss_product::viewCount"),

	// set key
	SET_VIEW("s_product::viewCount::");

	private String key;

}
