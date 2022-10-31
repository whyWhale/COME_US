package com.platform.order.order;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.order.exception.BusinessException;
import com.platform.order.order.exception.NotFoundResource;

@Transactional(readOnly = true)
@Service
public class OrderService {

	private static final int SUCCESS_STOCK_DECREASE = 1;
	private final ProductRepository productRepository;

	public OrderService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product findById(Long id) {
		return productRepository.findById(id).orElseThrow(
			() -> new NotFoundResource(
				MessageFormat.format("product id:{0} is not found.", 1)
			)
		);
	}

	@Transactional
	public void purchase(Long productId, int amount) {
		productRepository.findById(productId)
			.orElseThrow(
				() -> new NotFoundResource(
					MessageFormat.format("product id:{0} is not found.", productId))
			).decrese(amount);
	}

	@Transactional
	public void purchaseByPessimisticLock(Long productId, long amount) {
		productRepository.findByIdForUpdate(productId)
			.orElseThrow(
				() -> new NotFoundResource(
					MessageFormat.format("product id:{0} is not found.", productId))
			).decrese(amount);
	}

	@Transactional
	public void purchaseByNative(Long productId, long amount) {
		Integer affectedRow = productRepository.updateQuantity(productId, amount);

		if (affectedRow != SUCCESS_STOCK_DECREASE) {
			throw new BusinessException(
				MessageFormat.format("product id:{0} is out of stock.", productId)
			);
		}
	}
}
