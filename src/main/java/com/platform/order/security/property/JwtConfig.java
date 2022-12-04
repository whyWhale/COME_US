package com.platform.order.security.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import com.platform.order.security.Token;

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
	Token accessToken,
	Token refreshToken,
	String issuer,
	String secretKey
) {
}
