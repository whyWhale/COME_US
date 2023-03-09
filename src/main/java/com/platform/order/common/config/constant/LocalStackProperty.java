package com.platform.order.common.config.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "cloud.aws.s3")
public record LocalStackProperty(String endpoint,String bucket) {
}
