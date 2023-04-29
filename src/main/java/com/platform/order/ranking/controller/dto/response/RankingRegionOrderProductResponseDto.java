package com.platform.order.ranking.controller.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record RankingRegionOrderProductResponseDto(
	@Schema(description = "광역시도에서 주문이 많은 상품")
	List<ProductByCity> productsByCity,

	@Schema(description = "시군구에서 주문이 많은 상품")
	List<ProductByCountry> productsByCountry,

	@Schema(description = "읍면동에서 주문이 많은 상품")
	List<ProductByDistrict> productsByDistrict
) {
	public record ProductByCity(
		Long productId,
		String name,
		Long price,
		String categoryCode,
		String thumbnailPath
	) {
	}

	public record ProductByCountry(
		Long productId,
		String name,
		Long price,
		String categoryCode,
		String thumbnailPath
	) {
	}

	public record ProductByDistrict(
		Long productId,
		String name,
		Long price,
		String categoryCode,
		String thumbnailPath
	) {
	}

}
