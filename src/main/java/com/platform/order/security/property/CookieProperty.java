package com.platform.order.security.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.web.server.Cookie;

@ConstructorBinding
@ConfigurationProperties(prefix = "cookie")
public record CookieProperty(Boolean secure, Cookie.SameSite sameSite, String domain) {
}
