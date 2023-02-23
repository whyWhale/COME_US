package com.platform.order.product.domain.respository.custom;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.platform.order.product.domain.entity.ProductImageEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomProductImageRepositoryImpl implements CustomProductImageRepository {

	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public List<ProductImageEntity> saveAllInBulk(List<ProductImageEntity> productImages) {

		String sql = "INSERT INTO PRODUCT_IMAGE(product_id,origin_name, name, path, extension, size, arrangement) "
			+ "VALUES (?,?,?,?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(
			sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setLong(1, productImages.get(i).getProduct().getId());
					ps.setString(2, productImages.get(i).getOriginName());
					ps.setString(3, productImages.get(i).getName());
					ps.setString(4, productImages.get(i).getPath());
					ps.setString(5, productImages.get(i).getExtension());
					ps.setLong(6, productImages.get(i).getSize());
					ps.setLong(7, productImages.get(i).getArrangement());
				}

				@Override
				public int getBatchSize() {
					return productImages.size();
				}
			}
		);

		return productImages;
	}

	@Override
	public void deleteBatchByProductId(Long productId) {
		namedParameterJdbcTemplate.update("DELETE FROM product_image WHERE product_id = :productId",
			Collections.singletonMap("productId", productId));
	}

}
