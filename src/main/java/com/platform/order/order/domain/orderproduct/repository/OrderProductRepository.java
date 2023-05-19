package com.platform.order.order.domain.orderproduct.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Long>, CustomOrderProductRepository {
	@Query(value = "select op from OrderProductEntity op "
		+ "join fetch op.order "
		+ "join fetch op.product "
		+ "left join fetch op.userCoupon "
		+ "where op.id =:orderProductId and op.order.userId =:authId")
	Optional<OrderProductEntity> findByIdAndAuthId(
		@Param("orderProductId") Long orderProductId,
		@Param("authId") Long authId
	);

	@Query(value = "select op from OrderProductEntity op "
		+ "join fetch op.product "
		+ "where op.id =:orderProductId")
	Optional<OrderProductEntity> findByIdWithProduct(
		@Param("orderProductId") Long orderProductId);
}
