package com.platform.order.order.controller.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record Location(
	@Schema(description = "광역시도", required = true)
	@NotBlank
	String city,

	@Schema(description = "시군구", required = true)
	@NotBlank
	String country,

	@Schema(description = "읍면동", required = true)
	@NotBlank
	String district
) {

	public String toStringUntilCountry() {
		return city + country;
	}

	public String toStringUntilDistrict() {
		return city + country + district;
	}
}
