package com.platform.order.user.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserService  {

	Long save(OAuth2User oAuth2User,String provider);
	boolean isMatchProvider(String provider);
}
