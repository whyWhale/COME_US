package com.platform.order.common.security.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenService {
	private final RedisTemplate<String, String> redisTemplate;

	public void saveRefreshToken(Long userId, String refreshToken, long timeToLive) {
		redisTemplate.opsForValue()
			.set(userId.toString(), refreshToken, Duration.ofMillis(timeToLive));
	}

	public String findRefreshTokenByUserId(Long userId) {
		ValueOperations<String, String> operations = redisTemplate.opsForValue();

		return operations.get(userId.toString());
	}

	public void remove(Long userId) {
		redisTemplate.delete(userId.toString());
	}
}
