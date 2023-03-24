package com.platform.order.ranking.service;

import java.util.List;

import com.platform.order.product.controller.dto.response.product.RankingReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.RankingRegionOrderProductResponseDto;
import com.platform.order.product.controller.dto.response.product.RankingWishProductResponseDto;
import com.platform.order.product.domain.product.entity.ProductEntity;

public class RankingMapper {

	public List<RankingWishProductResponseDto> toRankingWishProductResponses(List<ProductEntity> rankingProducts) {
		return rankingProducts.stream()
			.map(product -> new RankingWishProductResponseDto(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}

	public List<RankingReadProductResponseDto> toRankingReadProductResponses(List<ProductEntity> rankingProducts) {
		return rankingProducts.stream()
			.map(product -> new RankingReadProductResponseDto(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}

	public RankingRegionOrderProductResponseDto toRankingRegionOrderProductResponses(
		List<ProductEntity> maximumOrderProductsByCity,
		List<ProductEntity> maximumOrderProductsByCountry,
		List<ProductEntity> maximumOrderProductsByDistrict
	) {

		List<RankingRegionOrderProductResponseDto.ProductByCity> productsByCity = toProductByCity(maximumOrderProductsByCity);
		List<RankingRegionOrderProductResponseDto.ProductByCountry> producstByCountry = toProductByCountry(maximumOrderProductsByCountry);
		List<RankingRegionOrderProductResponseDto.ProductByDistrict> productsByDistrict = toProductByDistrict(maximumOrderProductsByDistrict);

		return new RankingRegionOrderProductResponseDto(
			productsByCity,
			producstByCountry,
			productsByDistrict
		);
	}

	public List<RankingRegionOrderProductResponseDto.ProductByCity> toProductByCity(
		List<ProductEntity> maximumOrderProductsByCity) {
		return maximumOrderProductsByCity.stream()
			.map(product -> new RankingRegionOrderProductResponseDto.ProductByCity(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}

	public List<RankingRegionOrderProductResponseDto.ProductByCountry> toProductByCountry(
		List<ProductEntity> maximumOrderProductsByCity) {
		return maximumOrderProductsByCity.stream()
			.map(product -> new RankingRegionOrderProductResponseDto.ProductByCountry(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}

	public List<RankingRegionOrderProductResponseDto.ProductByDistrict> toProductByDistrict(
		List<ProductEntity> maximumOrderProductsByCity) {
		return maximumOrderProductsByCity.stream()
			.map(product -> new RankingRegionOrderProductResponseDto.ProductByDistrict(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}
}
