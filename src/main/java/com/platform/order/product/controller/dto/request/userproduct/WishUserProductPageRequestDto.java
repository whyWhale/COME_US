package com.platform.order.product.controller.dto.request.userproduct;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.platform.order.common.dto.offset.OffsetPageRequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class WishUserProductPageRequestDto extends OffsetPageRequestDto {
	@Schema(description = "최소 시간")
	private LocalDate lowerCreatedAt;

	@Schema(description = "최대 시간")
	private LocalDate upperCreatedAt;

	@Schema(description = "정렬 조건")
	private List<UserProductOrder> sorts;

	public WishUserProductPageRequestDto(Integer page, Integer size, LocalDate lowerCreatedAt, LocalDate upperCreatedAt,
		List<UserProductOrder> sorts) {
		super(page, size);
		this.lowerCreatedAt = lowerCreatedAt;
		this.upperCreatedAt = upperCreatedAt;
		this.sorts = sorts;
	}

	public Pageable toPageable() {
		if (sorts == null) {
			return PageRequest.of(super.page, super.size, Sort.by(Sort.Order.desc("createdAt")));
		}

		List<Sort.Order> orders = sorts.stream()
			.map(UserProductOrder::getPageableOrder)
			.toList();

		return PageRequest.of(super.page, super.size, Sort.by(orders));
	}

	@Getter
	@AllArgsConstructor
	public enum UserProductOrder {
		CREATED_ASC("createdAt", "asc"),
		CREATED_DESC("createdAt", "desc");

		String property;
		String direction;

		public Sort.Order getPageableOrder() {
			return this.direction.equals("desc") ?
				Sort.Order.desc(this.getProperty()) : Sort.Order.asc(this.getProperty());
		}
	}
}
