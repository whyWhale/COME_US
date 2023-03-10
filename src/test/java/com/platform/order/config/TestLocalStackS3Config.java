package com.platform.order.config;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @Document: aws s3 에뮬레이터
 */
@TestConfiguration
public class TestLocalStackS3Config {
	private static final DockerImageName LOCAL_STACK_IMAGE = DockerImageName.parse("localstack/localstack");

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Bean(initMethod = "start", destroyMethod = "stop")
	public LocalStackContainer localStackContainer() {
		DockerImageName parse = DockerImageName.parse("localstack/localstack");
		return new LocalStackContainer(LOCAL_STACK_IMAGE)
			.withServices(LocalStackContainer.Service.S3);
	}

	@Bean
	public AmazonS3 amazonS3(LocalStackContainer localStackContainer) {
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
			.withEndpointConfiguration(localStackContainer.getEndpointConfiguration(S3))
			.withCredentials(localStackContainer.getDefaultCredentialsProvider())
			.build();

		amazonS3.createBucket(bucket);
		return amazonS3;
	}
}
