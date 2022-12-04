package com.platform.order.security.property;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "security")
public record SecurityUrlProperty(UrlPatternConfig urlPatternConfig) {

	public record UrlPatternConfig(Map<String, String[]> ignoring, Map<String, String[]> permitAll) {
	}
}
