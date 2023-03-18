package com.platform.order.review.controller.dto.request;

import static com.platform.order.review.controller.dto.request.ReviewPageRequestDto.ReviewOrder.getDefaultOrder;
import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.platform.order.common.dto.offset.PageRequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ReviewPageRequestDto extends PageRequestDto {
	@Range(min = 1, max = 5)
	private Integer score;

	private List<ReviewOrder> sorts;

	public ReviewPageRequestDto(Integer page, Integer size, Integer score, List<ReviewOrder> sorts) {
		super(page, size);
		this.score = score;
		this.sorts = sorts;
	}

	public Pageable toPageable() {
		if (sorts == null || sorts.isEmpty()) {
			return PageRequest.of(
				super.page,
				super.size,
				Sort.by(getDefaultOrder())
			);
		}

		List<Sort.Order> orders = sorts.stream()
			.map(ReviewOrder::getPageableOrder)
			.toList();

		return PageRequest.of(
			super.page,
			super.size,
			Sort.by(orders)
		);
	}

	@Getter
	@AllArgsConstructor
	public enum ReviewOrder {
		CREATED_ASC("createdAt", "asc"),
		CREATED_DESC("createdAt", "desc"),
		SCORED_ASC("score", "asc"),
		SCORED_DESC("score", "desc");

		String property;
		String direction;

		public Sort.Order getPageableOrder() {
			return this.direction.equals("desc") ? desc(this.getProperty()) : asc(this.getProperty());
		}

		public static Sort.Order getDefaultOrder() {
			return CREATED_DESC.getPageableOrder();
		}
	}
}
