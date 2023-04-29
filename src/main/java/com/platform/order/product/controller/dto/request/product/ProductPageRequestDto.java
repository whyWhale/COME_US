package com.platform.order.product.controller.dto.request.product;

import java.util.List;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.platform.order.common.dto.offset.OffsetPageRequestDto;
import com.platform.order.common.validation.KeywordSearch;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public final class ProductPageRequestDto extends OffsetPageRequestDto {

	@Schema(description = "상품명(최소 2자이상 입력해야합니다.)")
	@KeywordSearch
	private String name;

	@Schema(description = "최대 가격")
	@PositiveOrZero
	private Long maximumPrice;

	@Schema(description = "최소 가격")
	@PositiveOrZero
	private Long minimumPrice;

	@Schema(description = "정렬 조건")
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
