package com.platform.order.common.config.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "cloud.aws.credentials")
public record AwsProperty(String accessKey, String secretKey) {
}
