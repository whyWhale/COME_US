package com.platform.order.product.domain.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.platform.order.product.domain.category.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	Optional<CategoryEntity> findByCode(String code);

	@Query(value = "select c from CategoryEntity c "
		+ "order by c.code")
	List<CategoryEntity> findAllOrderByCode();
}
