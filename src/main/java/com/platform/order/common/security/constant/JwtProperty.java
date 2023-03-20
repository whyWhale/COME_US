package com.platform.order.common.security.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import com.platform.order.common.security.model.Token;

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public record JwtProperty(
	Token accessToken,
	Token refreshToken,
	String issuer,
	String secretKey
) {
}
