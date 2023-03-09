package com.platform.order.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.platform.order.common.config.constant.AwsProperty;

import lombok.RequiredArgsConstructor;

@Profile({"dev"})
@RequiredArgsConstructor
@EnableConfigurationProperties(AwsProperty.class)
@Configuration
public class S3Config {

	private final AwsProperty awsProperty;

	@Bean
	public AmazonS3 amazonS3() {
		AWSCredentials credentials = new BasicAWSCredentials(awsProperty.accessKey(), awsProperty.secretKey());

		return AmazonS3ClientBuilder
			.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.AP_NORTHEAST_2)
			.build();
	}
}
