package com.platform.order.coupon.controller;

import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.order.common.config.WebSecurityConfig;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtConfig;
import com.platform.order.common.security.service.TokenService;
import com.platform.order.coupon.controller.CouponController;
import com.platform.order.coupon.controller.dto.request.CreateCouponRequestDto;
import com.platform.order.coupon.domain.entity.CouponType;
import com.platform.order.coupon.service.CouponService;
import com.platform.order.security.WithJwtMockUser;

@WithJwtMockUser
@WebMvcTest({CouponController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtConfig.class
})
class CouponControllerTest {
	final String URI_PREFIX = "/api/coupons";
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	TokenService tokenService;

	@MockBean
	CouponService couponService;

	@Test
	@DisplayName("쿠폰을 생성한다.")
	void testCreate() throws Exception {
		// given
		CreateCouponRequestDto createCouponRequest = new CreateCouponRequestDto(CouponType.FIXED, 10000L, 1000L,
			LocalDate.now().plusDays(60));
		// when
		ResultActions perform = mockMvc.perform(
			post(URI_PREFIX)
				.content(objectMapper.writeValueAsString(createCouponRequest))
				.contentType(MediaType.APPLICATION_JSON)
		);
		// then
		perform.andExpect(status().isOk());
		verify(couponService, times(1)).create(createCouponRequest);
	}

	@DisplayName("쿠폰을 생성할 때")
	@Nested
	class CreateCouponValidation {
		static Stream<Arguments> getPastDate() {
			return Stream.of(
				Arguments.arguments(LocalDate.now().minusDays(1)),
				Arguments.arguments(LocalDate.now().minusDays(2)),
				Arguments.arguments(LocalDate.now().minusDays(3))
			);
		}

		@Test
		@DisplayName("쿠폰 형식이 없으면 BadRequest로 응답한다.")
		void failCreateWithNullCouponType() throws Exception {
			//given
			CreateCouponRequestDto createCouponRequest = new CreateCouponRequestDto(null, 10000L, 1000L,
				LocalDate.now().plusDays(60));
			//when
			ResultActions perform = getPerform(createCouponRequest);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("비율 또는 고정 금액이 음수이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: 비율 또는 금액 {0}")
		@ValueSource(longs = {-1, -2, -3})
		void failCreateWithNegativeAmount(Long amount) throws Exception {
			//given
			CreateCouponRequestDto createCouponRequest = new CreateCouponRequestDto(CouponType.FIXED, amount, 1000L,
				LocalDate.now().plusDays(60));
			//when
			ResultActions perform = getPerform(createCouponRequest);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("수량이 음수이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: 수량 {0}")
		@ValueSource(longs = {-1, -2, -3})
		void failCreateWithNegativeQuantity(Long quantity) throws Exception {
			//given
			CreateCouponRequestDto createCouponRequest = new CreateCouponRequestDto(CouponType.FIXED, 1000L, quantity,
				LocalDate.now().plusDays(60));
			//when
			ResultActions perform = getPerform(createCouponRequest);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("만료일자가 과거이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: 날짜 {0}")
		@MethodSource("getPastDate")
		void failCreateWithNegativeQuantity(LocalDate pastDate) throws Exception {
			//given
			CreateCouponRequestDto createCouponRequest = new CreateCouponRequestDto(CouponType.FIXED, 1000L, 200L,
				pastDate);
			//when
			ResultActions perform = getPerform(createCouponRequest);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@NotNull
		ResultActions getPerform(CreateCouponRequestDto createCouponRequest) throws Exception {
			return mockMvc.perform(
				post(URI_PREFIX)
					.content(objectMapper.writeValueAsString(createCouponRequest))
					.contentType(MediaType.APPLICATION_JSON)
			);
		}

	}
}