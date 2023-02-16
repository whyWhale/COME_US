package com.platform.order.order.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.order.domain.entity.DeliveryEntity;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity,Long> {
}
