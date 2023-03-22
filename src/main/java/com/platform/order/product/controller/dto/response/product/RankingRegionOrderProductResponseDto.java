package com.platform.order.product.controller.dto.response.product;

import java.util.List;

public record RankingRegionOrderProductResponseDto(
	List<ProductByCity> productsByCity,
	List<ProductByCountry> productsByCountry,
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
