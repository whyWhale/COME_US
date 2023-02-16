package com.platform.order.order.domain.repository.custom;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.platform.order.order.domain.entity.OrderProductEntity;

@Repository
public interface CustomOrderProductRepository {
	List<OrderProductEntity> saveAllInBulk(List<OrderProductEntity> orderProducts);
}
