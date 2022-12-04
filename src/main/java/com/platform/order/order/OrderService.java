package com.platform.order.order;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.exception.BusinessException;
import com.platform.order.common.exception.ErrorCode;
import com.platform.order.common.exception.NotFoundResource;

@Transactional(readOnly = true)
@Service
public class OrderService {

	private static final int SUCCESS_STOCK_DECREASE = 1;
	private final ProductRepository productRepository;
	private final StockRepository stockRepository;

	public OrderService(ProductRepository productRepository, StockRepository stockRepository) {
		this.productRepository = productRepository;
		this.stockRepository = stockRepository;
	}

	public Product findById(Long id) {
		return productRepository.findById(id).orElseThrow(
			() -> new NotFoundResource(
				MessageFormat.format("product id:{0} is not found.", 1),
				ErrorCode.NOT_FOUND_RESOURCES)
		);
	}

	@Transactional
	public void purchase(Long productId, int amount) {
		productRepository.findById(productId)
			.orElseThrow(
				() -> new NotFoundResource(
					MessageFormat.format("product id:{0} is not found.", productId),
					ErrorCode.NOT_FOUND_RESOURCES)
			).decrese(amount);
	}

	@Transactional
	public void purchaseByPessimisticLock(Long productId, long amount) {
		productRepository.findByIdForUpdate(productId)
			.orElseThrow(
				() -> new NotFoundResource(
					MessageFormat.format("product id:{0} is not found.", productId),
					ErrorCode.NOT_FOUND_RESOURCES)
			).decrese(amount);
	}

	@Transactional
	public void purchaseByNative(Long productId, long amount) {
		Integer affectedRow = productRepository.updateQuantity(productId, amount);

		if (affectedRow != SUCCESS_STOCK_DECREASE) {
			throw new BusinessException(
				MessageFormat.format("product id:{0} is out of stock.", productId),
				ErrorCode.NOT_FOUND_RESOURCES
			);
		}
	}

	@Transactional
	public void divideModel(Long productId, long amout) {
		Integer changedResult = stockRepository.updateQuantity(productId, amout);

		if (changedResult != SUCCESS_STOCK_DECREASE) {
			throw new BusinessException(
				MessageFormat.format("product id:{0} is out of stock.", productId),
				ErrorCode.NOT_FOUND_RESOURCES
			);
		}
	}
}
