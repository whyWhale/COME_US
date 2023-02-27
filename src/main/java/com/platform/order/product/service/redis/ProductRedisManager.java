package com.platform.order.product.service.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductRedisManager{
	WISH( "wishCount");

	private String key;

}
