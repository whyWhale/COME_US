package com.platform.order.product.controller.dto.request.product;

import java.util.List;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.platform.order.common.dto.offset.PageRequestDto;
import com.platform.order.common.validation.KeywordSearch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public final class ProductPageRequestDto extends PageRequestDto {

	@KeywordSearch
	private String name;
	@PositiveOrZero
	private Long maximumPrice;
	@PositiveOrZero
	private Long minimumPrice;
	@Size(max = 2)
	private List<ProductCondition> sorts;

	public ProductPageRequestDto(Integer page, Integer size, String name, Long maximumPrice, Long minimumPrice,
		List<ProductCondition> sorts) {
		super(page, size);
		this.name = name;
		this.maximumPrice = maximumPrice;
		this.minimumPrice = minimumPrice;
		this.sorts = sorts;
	}

	public Pageable toPageable() {
		if (sorts == null) {
			return PageRequest.of(super.page, super.size, Sort.by(Sort.Order.desc("createdAt")));
		}

		List<Sort.Order> orders = sorts.stream()
			.map(ProductCondition::getPageableOrder)
			.toList();

		return PageRequest.of(super.page, super.size, Sort.by(orders));
	}

	@Getter
	@AllArgsConstructor
	public enum ProductCondition {
		CREATED_ASC("createdAt", "asc"),
		CREATED_DESC("createdAt", "desc"),
		PRICE_ASC("price", "asc"),
		PRICE_DESC("price", "desc");
		String property;
		String direction;

		public Sort.Order getPageableOrder() {
			return this.direction.equals("desc") ?
				Sort.Order.desc(this.getProperty()) : Sort.Order.asc(this.getProperty());
		}
	}
}
