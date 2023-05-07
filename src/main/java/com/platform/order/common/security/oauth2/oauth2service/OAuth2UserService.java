package com.platform.order.common.security.oauth2.oauth2service;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserService  {

	Long save(OAuth2User oAuth2User,String provider);
	boolean isMatchProvider(String provider);
}
