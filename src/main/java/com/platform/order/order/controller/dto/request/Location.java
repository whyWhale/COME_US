package com.platform.order.order.controller.dto.request;

import javax.validation.constraints.NotBlank;

public record Location(

	@NotBlank
	String city,

	@NotBlank
	String country,

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
