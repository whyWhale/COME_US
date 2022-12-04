package com.platform.order.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private final Object principal;
	private String credentials;

	private JwtAuthenticationToken(
		Object principal,
		String credentials,
		Collection<? extends GrantedAuthority> authorities
	) {
		super(authorities);
		super.setAuthenticated(true);
		this.principal = principal;
		this.credentials = credentials;
	}

	public static JwtAuthenticationToken create(
		Object principal,
		Collection<? extends GrantedAuthority> authorities
	) {
		return new JwtAuthenticationToken(principal, null, authorities);
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}
