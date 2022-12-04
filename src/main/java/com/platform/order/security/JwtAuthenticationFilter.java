package com.platform.order.security;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.platform.order.security.exception.TokenNotFoundException;
import com.platform.order.security.property.CookieProperty;
import com.platform.order.security.property.JwtConfig;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final JwtProviderManager jwtProviderManager;
	private final JwtConfig jwtConfig;
	private final CookieProperty cookieProperty;

	public JwtAuthenticationFilter(
		JwtProviderManager jwtProviderManager,
		JwtConfig jwtConfig,
		CookieProperty cookieProperty
	) {
		this.jwtConfig = jwtConfig;
		this.cookieProperty = cookieProperty;
		this.jwtProviderManager = jwtProviderManager;
	}

	/**
	 * note: 인증된 사용자 인지 토큰 체크
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			String accessToken = jwtProviderManager.extractAccessToken(request);
			authenticate(accessToken, request, response);
		} catch (TokenNotFoundException e) {
			logger.warn("token is not exist..");
		} finally {
			filterChain.doFilter(request, response);
		}
	}

	private void authenticate(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			JwtProviderManager.CustomClaim verifiedClaim = jwtProviderManager.verify(accessToken);
			JwtAuthenticationToken authenticationToken = createAuthenticationToken(verifiedClaim, request, accessToken);

			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		} catch (TokenExpiredException e) {
			logger.warn(e.getMessage());
			reIssueAccessToken(accessToken, request, response);
		} catch (JWTVerificationException e) {
			logger.warn(e.getMessage());
		}
	}

	private void reIssueAccessToken(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			String refreshToken = jwtProviderManager.extractRefreshToken(request);
			jwtProviderManager.verifyRefreshToken(accessToken, refreshToken);
			JwtProviderManager.CustomClaim claim = jwtProviderManager.decode(accessToken);
			String reIssuedToken = jwtProviderManager.generateAccessToken(claim);
			JwtProviderManager.CustomClaim verifiedClaim = jwtProviderManager.verify(reIssuedToken);
			JwtAuthenticationToken authenticationToken = createAuthenticationToken(verifiedClaim, request, reIssuedToken);

			ResponseCookie cookie = ResponseCookie.from(
					jwtConfig.accessToken().header(),
					reIssuedToken
				).path("/")
				.httpOnly(true)
				.sameSite(cookieProperty.sameSite().attributeValue())
				.domain(cookieProperty.domain())
				.secure(cookieProperty.secure())
				.maxAge(jwtConfig.refreshToken().expirySeconds())
				.build();

			response.addHeader(SET_COOKIE, cookie.toString());
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		} catch (EntityNotFoundException | TokenNotFoundException | JWTVerificationException e) {
			logger.warn("refresh token expire.");
		}
	}

	private JwtAuthenticationToken createAuthenticationToken(
		JwtProviderManager.CustomClaim claims,
		HttpServletRequest request,
		String accessToken
	) {
		List<GrantedAuthority> authorities = jwtProviderManager.getAuthorities(claims);

		if (claims.userId == null || authorities.isEmpty()) {
			throw new JWTDecodeException("Decode Error");
		}

		JwtAuthentication authentication = new JwtAuthentication(claims.userId, accessToken);
		JwtAuthenticationToken authenticationToken = JwtAuthenticationToken.create(authentication, authorities);

		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		return authenticationToken;
	}
}
