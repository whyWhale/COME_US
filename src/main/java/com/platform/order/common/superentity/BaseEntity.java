package com.platform.order.common.superentity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(updatable = false, nullable = false)
	@CreatedDate
	protected LocalDateTime createdAt;

	@Column(nullable = false)
	@LastModifiedDate
	protected LocalDateTime updatedAt;

	@Column(name = "deleted", nullable = false)
	protected Boolean isDeleted = false;
}
