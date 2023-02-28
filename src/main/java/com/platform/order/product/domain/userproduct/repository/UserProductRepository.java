package com.platform.order.product.domain.userproduct.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.order.product.domain.userproduct.entity.UserProductEntity;

public interface UserProductRepository extends JpaRepository<UserProductEntity, Long>, CustomUserProductRepository {
	@Query("select up from UserProductEntity up join fetch up.product p join UserEntity u on up.wisher.id = u.id where up.id =:userProductId and u.id =:userId")
	Optional<UserProductEntity> findByIdAndWisherId(@Param("userProductId") Long userProductId, @Param("userId") Long userId);
}
