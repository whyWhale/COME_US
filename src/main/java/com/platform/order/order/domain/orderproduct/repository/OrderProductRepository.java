package com.platform.order.order.domain.orderproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Long>, CustomOrderProductRepository {
}
