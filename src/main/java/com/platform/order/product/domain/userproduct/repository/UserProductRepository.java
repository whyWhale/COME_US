package com.platform.order.product.domain.userproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.product.domain.userproduct.entity.UserProductEntity;

public interface UserProductRepository extends JpaRepository<UserProductEntity, Long>, CustomUserProductRepository {
}
