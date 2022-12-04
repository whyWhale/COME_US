package com.platform.order.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {
	@Modifying(clearAutomatically = true)
	@Query(value = "update stock s set s.quantity=s.quantity-:amount where s.product_id =:productId and s.quantity >= :amount", nativeQuery = true)
	Integer updateQuantity(@Param("productId") Long productId, @Param("amount") Long amount);
}
