package com.platform.order;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class BaseSpringBootTest {
	@Container
	static final GenericContainer<?> redis = new GenericContainer<>(
		DockerImageName.parse("redis:latest")).withExposedPorts(6379);

	static {
		redis.start();
		System.setProperty("spring.redis.host", redis.getHost());
		System.setProperty("spring.redis.port", redis.getMappedPort(6379).toString());
	}
}
