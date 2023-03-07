package com.platform.order.user.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Where;

import com.platform.order.common.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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

	@NotBlank
	@Column(unique = true)
	@Size(min = 8, max = 24)
	private String username;

	@NotBlank
	@Column(nullable = false)
	private String password;

	@NotBlank
	@Pattern(regexp = "^[가-힣|a-z|A-Z|0-9|_.#]+$")
	@Size(min = 1, max = 20)
	private String nickName;

	@Email
	private String email;

	@Enumerated(EnumType.STRING)
	private Role role;
}


