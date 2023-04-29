package com.platform.order.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.platform.order.common.cache.RedisCacheKey;
import com.platform.order.common.config.constant.RedisProperty;

import lombok.RequiredArgsConstructor;

@Profile({"local", "prod"})
@Configuration
@RequiredArgsConstructor
@EnableCaching
@EnableRedisRepositories
@EnableConfigurationProperties(RedisProperty.class)
public class RedisConfig {
	private final RedisProperty redisProperty;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(
			new RedisClusterConfiguration(redisProperty.nodes())
		);
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		return redisTemplate;
	}

	@DependsOn(value = {"redisConnectionFactory"})
	@Bean
	public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext
				.SerializationPair
				.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext
				.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer()));
		Map<String, RedisCacheConfiguration> cacheConfiguration = new HashMap<>();

		cacheConfiguration.put(RedisCacheKey.CATEGORIES.getCacheKey(),
			redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)));

		return RedisCacheManager
			.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(redisCacheConfiguration)
			.withInitialCacheConfigurations(cacheConfiguration)
			.build();
	}

}
