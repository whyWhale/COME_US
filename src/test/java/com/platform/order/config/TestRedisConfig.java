package com.platform.order.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.platform.order.common.config.RedisConfig;
import com.platform.order.common.config.property.RedisProperty;

import redis.embedded.RedisServer;

@Configuration
@EnableRedisRepositories
@EnableConfigurationProperties(RedisProperty.class)
public class TestRedisConfig {
	private RedisServer redisServer;
	private RedisProperty redisProperty;

	public TestRedisConfig(RedisProperty redisProperty) {
		this.redisProperty = redisProperty;
		redisServer = new RedisServer(redisProperty.port());
	}

	@PostConstruct
	public void startRedis() throws IOException {
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		redisServer.stop();
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisProperty.host(), redisProperty.port());
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		return redisTemplate;
	}
}