package com.platform.order.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.platform.order.security.property.CookieProperty;
import com.platform.order.security.property.JwtConfig;
import com.platform.order.security.property.SecurityUrlProperty;

@EnableConfigurationProperties({SecurityUrlProperty.class, JwtConfig.class, CookieProperty.class})
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private final JwtProviderManager jwtProviderManager;
	private final CookieProperty cookieProperty;
	private final SecurityUrlProperty securityUrlProperty;

	private final JwtConfig jwtConfig;

	public WebSecurityConfig(
		JwtProviderManager jwtProviderManager,
		CookieProperty cookieProperty,
		SecurityUrlProperty securitySettingProperty,
		JwtConfig jwtConfig
	) {
		this.jwtProviderManager = jwtProviderManager;
		this.cookieProperty = cookieProperty;
		this.securityUrlProperty = securitySettingProperty;
		this.jwtConfig = jwtConfig;
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

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
				new JwtAuthenticationFilter(jwtProviderManager, jwtConfig, cookieProperty),
				UsernamePasswordAuthenticationFilter.class
			).cors();

		return httpSecurity.build();
	}

	private String[] getIgnoringUrl(HttpMethod httpMethod) {
		return this.securityUrlProperty.urlPatternConfig().ignoring().get(httpMethod.name());
	}
}

