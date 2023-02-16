package com.platform.order.order.domain.repository.custom;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.platform.order.order.domain.entity.OrderProductEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomOrderProductRepositoryImpl implements CustomOrderProductRepository {

	private final int DEFAULT_BATCH_SIZE = 1000;
	private final JdbcTemplate jdbcTemplate;

	@Override
	public List<OrderProductEntity> saveAllInBulk(List<OrderProductEntity> orderProducts) {
		List<OrderProductEntity> batches = new ArrayList<>();

		orderProducts.forEach(orderProduct -> {
			batches.add(orderProduct);

			if (batches.size() % DEFAULT_BATCH_SIZE == 0) {
				batchSave(batches);
			}
		});

		if (!batches.isEmpty()) {
			batchSave(batches);
		}

		return orderProducts;
	}

	private void batchSave(List<OrderProductEntity> orderProducts) {
		jdbcTemplate.batchUpdate(
			"INSERT INTO ORDER_PRODUCT(order_id, product_id, order_quantity, price) "
				+ "VALUES (?,?,?,?)", new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setLong(1, orderProducts.get(i).getOrder().getId());
					ps.setLong(2, orderProducts.get(i).getProduct().getId());
					ps.setLong(3, orderProducts.get(i).getOrderQuantity());
					ps.setLong(4, orderProducts.get(i).getPrice());
				}

				@Override
				public int getBatchSize() {
					return orderProducts.size();
				}
			}
		);
	}
}
