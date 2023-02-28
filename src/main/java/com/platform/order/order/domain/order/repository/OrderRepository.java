package com.platform.order.order.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.order.domain.order.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
