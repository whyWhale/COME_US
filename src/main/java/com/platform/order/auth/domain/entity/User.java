package com.platform.order.auth.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.platform.order.common.BaseEntity;

import lombok.Builder;

@Table(name = "users")
@Entity
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	protected User() {
	}

	@Builder
	public User(Long id, String username, String password, String nickName, String email, Role role) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.email = email;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getNickName() {
		return nickName;
	}

	public String getEmail() {
		return email;
	}

	public Role getRole() {
		return role;
	}
}


