package com.platform.order.product.domain.productthumbnail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.product.domain.productthumbnail.entity.ProductThumbnailEntity;

public interface ProductThumbnailRepository extends JpaRepository<ProductThumbnailEntity, Long> {
}
