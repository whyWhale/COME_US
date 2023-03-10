package com.platform.order.common.config.constant;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.redis.cluster")
public record RedisProperty(List<String> nodes) {
}
