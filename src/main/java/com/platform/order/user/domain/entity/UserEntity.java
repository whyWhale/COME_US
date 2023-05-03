package com.platform.order.user.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.platform.order.common.superentity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "deleted=false")
@Table(name = "users")
@Entity
public class UserEntity extends BaseEntity {

	private String username;

	private String password;

	private String nickName;

	private String email;

	private String provider;

	private String providerId;

	@Enumerated(EnumType.STRING)
	private Role role;
}


