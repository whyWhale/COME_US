package com.platform.order.common.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.platform.order.common.security.constant.JwtConfig;
import com.platform.order.common.security.exception.TokenNotFoundException;
import com.platform.order.common.security.service.TokenService;

import lombok.Builder;

@Component
public class JwtProviderManager {

	private final JwtConfig jwtConfig;
	private final Algorithm algorithm;
	private final JWTVerifier jwtVerifier;
	private final TokenService tokenService;

	public JwtProviderManager(JwtConfig jwtConfig, TokenService tokenService) {
		this.jwtConfig = jwtConfig;
		this.tokenService = tokenService;
		this.algorithm = Algorithm.HMAC512(jwtConfig.secretKey());
		this.jwtVerifier = JWT.require(algorithm)
			.withIssuer(this.jwtConfig.issuer())
			.build();
	}

	public String generateAccessToken(CustomClaim customClaim) {
		JWTCreator.Builder jwtBuilder = JWT.create();
		Date now = new Date();

		jwtBuilder.withSubject(customClaim.userId.toString());
		jwtBuilder.withIssuer(this.jwtConfig.issuer());
		jwtBuilder.withIssuedAt(now);
		jwtBuilder.withExpiresAt(new Date(now.getTime() + jwtConfig.accessToken().expirySeconds()));
		jwtBuilder.withClaim("userId", customClaim.userId);
		jwtBuilder.withArrayClaim("roles", customClaim.roles);

		return jwtBuilder.sign(algorithm);
	}

	public String generateRefreshToken(Long userId) {
		Date now = new Date();

		JWTCreator.Builder builder = JWT.create();
		builder.withIssuer(this.jwtConfig.issuer());
		builder.withIssuedAt(now);
		builder.withExpiresAt(new Date(now.getTime() + jwtConfig.refreshToken().expirySeconds()));

		String token = builder.sign(this.algorithm);

		tokenService.saveRefreshToken(userId, token, jwtConfig.refreshToken().expirySeconds());

		return token;
	}

	public String extractAccessToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			throw new TokenNotFoundException("AccessToken not found");
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwtConfig.accessToken().header()))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow(() -> new TokenNotFoundException("access token value null."));
	}

	public JwtProviderManager.CustomClaim verify(String accessToken) {
		DecodedJWT decodedJWT = this.jwtVerifier.verify(accessToken);

		return new CustomClaim(decodedJWT);
	}

	public List<GrantedAuthority> getAuthorities(CustomClaim claims) {
		String[] roles = claims.roles;

		return roles.length == 0 ? Collections.emptyList() :
			Arrays.stream(roles)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	public String extractRefreshToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			throw new TokenNotFoundException("RefreshToken not found");
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwtConfig.refreshToken().header()))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow(() -> new TokenNotFoundException("access token value null."));
	}

	public void verifyRefreshToken(String accessToken, String refreshToken) {
		Long userId = decode(accessToken).userId;
		String savedReFreshToken = tokenService.findRefreshTokenByUserId(userId);

		if (!refreshToken.equals(savedReFreshToken)) {
			throw new JWTVerificationException("not match refresh token.");
		}
	}

	public CustomClaim decode(String accessToken) {
		return new CustomClaim(JWT.decode(accessToken));
	}

	public void removeRefreshToken(Long id) {
		tokenService.remove(id);
	}

	public static class CustomClaim {
		Long userId;
		String[] roles;
		Date issuedAt;
		Date expiredAt;

		private CustomClaim() {
		}

		@Builder
		CustomClaim(Long userId, String[] roles, Date issuedAt, Date expiredAt) {
			this.userId = userId;
			this.roles = roles;
			this.issuedAt = issuedAt;
			this.expiredAt = expiredAt;
		}

		CustomClaim(DecodedJWT decodedJWT) {
			Claim userId = decodedJWT.getClaim("userId");

			if (!userId.isNull()) {
				this.userId = userId.asLong();
			}

			Claim roles = decodedJWT.getClaim("roles");

			if (!roles.isNull()) {
				this.roles = roles.asArray(String.class);
			}

			this.issuedAt = decodedJWT.getIssuedAt();
			this.expiredAt = decodedJWT.getExpiresAt();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;

			if (o == null || getClass() != o.getClass())
				return false;

			CustomClaim that = (CustomClaim)o;

			return new EqualsBuilder().append(userId, that.userId)
				.append(roles, that.roles)
				.append(issuedAt, that.issuedAt)
				.append(expiredAt, that.expiredAt)
				.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37).append(userId)
				.append(roles)
				.append(issuedAt)
				.append(expiredAt)
				.toHashCode();
		}
	}

}
