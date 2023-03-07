package com.platform.order.testenv;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import com.platform.order.config.LocalStackS3Config;

@ActiveProfiles("application-container")
@Import({LocalStackS3Config.class})
@SpringBootTest
public class IntegrationTest {
	@Container
	static final GenericContainer<?> redis = new GenericContainer<>(
		DockerImageName.parse("redis:latest")).withExposedPorts(6379);

	static {
		redis.start();
		System.setProperty("spring.redis.host", redis.getHost());
		System.setProperty("spring.redis.port", redis.getMappedPort(6379).toString());
	}
}
