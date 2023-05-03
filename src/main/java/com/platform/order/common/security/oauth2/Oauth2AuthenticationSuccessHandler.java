package com.platform.order.common.security.oauth2;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.platform.order.authentication.controller.dto.response.LoginAuthResponseDto;
import com.platform.order.authentication.controller.dto.response.TokenResponseDto;
import com.platform.order.authentication.controller.handler.LoginSuccessHandler;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.JwtProviderManager.CustomClaim;
import com.platform.order.common.security.constant.JwtProperty;
import com.platform.order.common.security.exception.OAuth2Exception;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.service.UserService;

@Component
public class Oauth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private final UserService userService;
	private final JwtProperty jwtProperty;
	private final LoginSuccessHandler loginSuccessHandler;
	private final JwtProviderManager jwtProviderManager;

	public Oauth2AuthenticationSuccessHandler(
		UserService userService,
		JwtProperty jwtProperty, LoginSuccessHandler loginSuccessHandler, JwtProviderManager jwtProviderManager
	) {
		this.userService = userService;
		this.jwtProperty = jwtProperty;
		this.loginSuccessHandler = loginSuccessHandler;
		this.jwtProviderManager = jwtProviderManager;
	}

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws ServletException, IOException {
		if (authentication instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken)authentication;
			String provider = authenticationToken.getAuthorizedClientRegistrationId();
			OAuth2User oAuth2User = authenticationToken.getPrincipal();
			String providerId = oAuth2User.getName();
			Map<String, Object> attributes = oAuth2User.getAttributes();
			if (attributes == null) {
				throw new OAuth2Exception("oauth structure check...");
			}

			Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
			if (properties == null) {
				throw new OAuth2Exception("oauth structure check...");
			}

			String nickname = (String)properties.get("nickname");
			Long id = userService.saveForOAuth2(provider, providerId, nickname);
			CustomClaim claim = createClaim(id);
			String accessToken = jwtProviderManager.generateAccessToken(claim);
			String refreshToken = jwtProviderManager.generateRefreshToken(id);
			LoginAuthResponseDto loginResponseDto = toLoginResponse(accessToken, refreshToken, jwtProperty);
			loginSuccessHandler.onLoginSuccess(response, loginResponseDto);

			return;
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}

	private CustomClaim createClaim(Long id) {
		return CustomClaim.builder()
			.userId(id)
			.roles(new String[] {Role.USER.getKey()})
			.build();
	}

	private LoginAuthResponseDto toLoginResponse(String accessToken, String refreshToken, JwtProperty jwtProperty) {
		return new LoginAuthResponseDto(
			new TokenResponseDto(
				jwtProperty.accessToken().header(),
				accessToken,
				jwtProperty.accessToken().expirySeconds()
			),
			new TokenResponseDto(
				jwtProperty.refreshToken().header(),
				refreshToken,
				jwtProperty.refreshToken().expirySeconds()
			)
		);
	}

}


