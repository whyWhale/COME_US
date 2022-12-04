package com.platform.order.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithJwtMockUserCustomFactory implements WithSecurityContextFactory<WithJwtMockUser> {
	@Override
	public SecurityContext createSecurityContext(WithJwtMockUser withMockUser) {

		JwtAuthenticationToken jwtAuthenticationToken = JwtAuthenticationToken.create(
			new JwtAuthentication(withMockUser.id(), withMockUser.token()),
			AuthorityUtils.createAuthorityList(withMockUser.role().getKey())
		);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(jwtAuthenticationToken);

		return context;
	}
}
