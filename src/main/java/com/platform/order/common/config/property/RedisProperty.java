package com.platform.order.common.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.redis")
public record RedisProperty(String host, int port) {
}
