package com.platform.order.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.platform.order.common.config.constant.AwsProperty;
import com.platform.order.common.config.constant.LocalStackProperty;

import lombok.RequiredArgsConstructor;

@Profile("local")
@RequiredArgsConstructor
@EnableConfigurationProperties({LocalStackProperty.class, AwsProperty.class})
@Configuration
public class LocalStackConfig {

	private final AwsProperty awsProperty;

	private final LocalStackProperty localStackProperty;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	public AmazonS3 amazonS3() {
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
			.withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration(localStackProperty.endpoint(), region)
			)
			.withCredentials(new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(awsProperty.accessKey(), awsProperty.secretKey()))
			).build();

		if (!amazonS3.doesBucketExistV2(localStackProperty.bucket())) {
			amazonS3.createBucket(new CreateBucketRequest(localStackProperty.bucket(), region));
		}

		return amazonS3;
	}

}
