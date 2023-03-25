package com.platform.order.product.domain.category.entity;

import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.platform.order.common.superentity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted=false")
@Table(name = "category")
@Entity
public class CategoryEntity extends BaseEntity {
	private String name;
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	private CategoryEntity parent;

	@OneToMany(mappedBy = "parent")
	private List<CategoryEntity> childs;

	public Optional<CategoryEntity> getParent() {
		return Optional.ofNullable(this.parent);
	}
}
