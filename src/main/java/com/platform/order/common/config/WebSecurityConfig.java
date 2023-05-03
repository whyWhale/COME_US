package com.platform.order.common.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import com.platform.order.common.security.JwtAuthenticationFilter;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.CookieProperty;
import com.platform.order.common.security.constant.JwtProperty;
import com.platform.order.common.security.constant.SecurityUrlProperty;
import com.platform.order.common.security.oauth2.Oauth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({SecurityUrlProperty.class, JwtProperty.class, CookieProperty.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig {

	private final JwtProviderManager jwtProviderManager;
	private final CookieProperty cookieProperty;
	private final SecurityUrlProperty securityUrlProperty;
	private final JwtProperty jwtProperty;
	private final Oauth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.antMatchers(HttpMethod.GET, this.getIgnoringUrl(HttpMethod.GET))
			.antMatchers(HttpMethod.POST, this.getIgnoringUrl(HttpMethod.POST))
			.antMatchers(HttpMethod.PATCH, this.getIgnoringUrl(HttpMethod.PATCH))
			.antMatchers(HttpMethod.DELETE, this.getIgnoringUrl(HttpMethod.PUT))
			.antMatchers(HttpMethod.PUT, this.getIgnoringUrl(HttpMethod.DELETE))
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint() {
		return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.authorizeRequests()
			.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			.antMatchers(HttpMethod.GET, this.securityUrlProperty.urlPatternConfig().permitAll().get("GET"))
			.permitAll()
			.antMatchers(HttpMethod.POST, this.securityUrlProperty.urlPatternConfig().permitAll().get("POST"))
			.permitAll()
			.antMatchers(HttpMethod.PATCH, this.securityUrlProperty.urlPatternConfig().permitAll().get("PATCH"))
			.permitAll()
			.antMatchers(HttpMethod.DELETE, this.securityUrlProperty.urlPatternConfig().permitAll().get("DELETE"))
			.permitAll()
			.antMatchers(HttpMethod.PUT, this.securityUrlProperty.urlPatternConfig().permitAll().get("PUT"))
			.permitAll()
			.antMatchers(HttpMethod.OPTIONS, this.securityUrlProperty.urlPatternConfig().permitAll().get("OPTIONS"))
			.permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin().disable()
			.csrf().disable()
			.headers().disable()
			.httpBasic().disable()
			.rememberMe().disable()
			.logout().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(authenticationEntryPoint())
			.and()
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtProviderManager, jwtProperty, cookieProperty),
				UsernamePasswordAuthenticationFilter.class
			)
			.cors()
			.and()
			.oauth2Login()
			.authorizationEndpoint()
			.and()
			.successHandler(oauth2AuthenticationSuccessHandler);

		return httpSecurity.build();
	}

	private String[] getIgnoringUrl(HttpMethod httpMethod) {
		return this.securityUrlProperty.urlPatternConfig().ignoring().get(httpMethod.name());
	}
}

