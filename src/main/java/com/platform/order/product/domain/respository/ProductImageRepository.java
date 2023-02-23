package com.platform.order.product.domain.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.entity.ProductImageEntity;
import com.platform.order.product.domain.respository.custom.CustomProductImageRepository;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity,Long>, CustomProductImageRepository {
	List<ProductImageEntity> findByProduct(ProductEntity product);
}
