package com.platform.order.review.service.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewRedisKeyManager {
	REVIEW_PRODUCT_LOCK_PREFIX("review::product::"),
	REVIEW_PRODUCT_KEY_PREFIX("review::product::");

	private String key;

}
