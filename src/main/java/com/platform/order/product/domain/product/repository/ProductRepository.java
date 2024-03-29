package com.platform.order.product.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.custom.CustomProductRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, CustomProductRepository {

	@Modifying(clearAutomatically = true)
	@Query(value = "update product p "
		+ "set p.quantity=p.quantity-:orderQuantity "
		+ "where p.id =:productId and"
		+ " p.quantity >=:orderQuantity"
		, nativeQuery = true)
	Integer updateQuantity(
		@Param("productId") Long productId,
		@Param("orderQuantity") Long orderQuantity
	);

	List<ProductEntity> findByIdIn(List<Long> ids);

	@Query(value = "select p from ProductEntity p "
		+ "join fetch p.category c "
		+ "join fetch p.productThumbnail t "
		+ "where p.id in :productIds")
	List<ProductEntity> findByIdInWithCategoryAndThumbnail(@Param("productIds") List<Long> ids);

	@Query(value = "select p from ProductEntity p "
		+ "join fetch p.category c "
		+ "where p.id =:productId")
	Optional<ProductEntity> findByIdWithCategory(@Param("productId") Long id);

	@Query(value = "select p from ProductEntity p "
		+ "join fetch p.category c "
		+ "join fetch p.productThumbnail")
	Optional<ProductEntity> findByIdWithCategoryAndThumbnail(Long productId);

}
