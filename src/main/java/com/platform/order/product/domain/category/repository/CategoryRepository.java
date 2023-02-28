package com.platform.order.product.domain.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.product.domain.category.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
	Optional<CategoryEntity> findByCode(String code);
}
