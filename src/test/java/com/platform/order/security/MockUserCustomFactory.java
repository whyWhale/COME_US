package com.platform.order.security;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.common.security.model.JwtAuthenticationToken;
import com.platform.order.user.domain.entity.Role;

public class MockUserCustomFactory implements WithSecurityContextFactory<WithJwtMockUser> {
	@Override
	public SecurityContext createSecurityContext(WithJwtMockUser withMockUser) {
		JwtAuthentication principal = new JwtAuthentication(withMockUser.id(), withMockUser.token());
		List<SimpleGrantedAuthority> authorities = getAuthorities(withMockUser);
		var jwtAuthenticationToken = JwtAuthenticationToken.create(principal, authorities);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(jwtAuthenticationToken);

		return context;
	}

	@NotNull
	private List<SimpleGrantedAuthority> getAuthorities(WithJwtMockUser withMockUser) {
		return Arrays.stream(withMockUser.role())
			.map(Role::getKey)
			.map(SimpleGrantedAuthority::new)
			.toList();
	}
}
