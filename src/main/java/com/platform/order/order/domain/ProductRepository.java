package com.platform.order.order.domain;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.order.order.domain.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Product p where p.id =:productId")
	Optional<Product> findByIdForUpdate(@Param("productId") Long productId);

	@Modifying(clearAutomatically = true)
	@Query(value = "update product p set p.quantity=p.quantity-:amount where p.id =:productId and p.quantity >= :amount", nativeQuery = true)
	Integer updateQuantity(@Param("productId") Long productId, @Param("amount") Long amount);
}
