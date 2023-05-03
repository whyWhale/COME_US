package com.platform.order.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

import com.platform.order.authentication.controller.AuthenticationController;
import com.platform.order.authentication.controller.handler.LoginSuccessHandler;
import com.platform.order.authentication.controller.handler.LogoutSuccessHandler;
import com.platform.order.common.config.WebSecurityConfig;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtProperty;
import com.platform.order.common.security.oauth2.Oauth2AuthenticationSuccessHandler;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto.OrderProductRequestDto;
import com.platform.order.order.controller.dto.request.Location;
import com.platform.order.order.controller.dto.request.OrderPageRequestDto;
import com.platform.order.order.service.OrderService;
import com.platform.order.security.WithJwtMockUser;
import com.platform.order.testenv.ControllerTest;
import com.platform.order.utils.ParameterUtils;

@WithJwtMockUser
@WebMvcTest({OrderController.class,
	AuthenticationController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	LoginSuccessHandler.class,
	LogoutSuccessHandler.class,
	Oauth2AuthenticationSuccessHandler.class,
	JwtProperty.class})
class OrderControllerTest extends ControllerTest {
	final String URI_PREFIX = "/api/orders";

	@MockBean
	OrderService orderService;

	Long buyingProductId = 1L;
	String address = "서울특별시 강남구 대치동";
	String zipCode = "123-12";
	Location location = new Location("서울특별시", "강남구", "대치동");

	@Test
	@DisplayName("상품을 주문한다.")
	void testOrder() throws Exception {
		//given
		Long orderQuantity = 1L;
		var orderProductsRequest = new OrderProductRequestDto(buyingProductId, orderQuantity, null);
		var requestDto = new CreateOrderRequestDto(address, zipCode, List.of(orderProductsRequest), location);
		//when
		ResultActions perform = mockMvc.perform(
			post(URI_PREFIX)
				.content(objectMapper.writeValueAsString(requestDto))
				.contentType(APPLICATION_JSON)
		);
		//then
		perform.andExpect(status().isOk());
		verify(orderService, times(1)).order(1L, requestDto);
	}

	@DisplayName("상품 주문시 ")
	@Nested
	class CreateOrderValidation {
		static Stream<Arguments> provideNullWithProductIdOrOrderQuantity() {
			return Stream.of(
				Arguments.of(null, 1L),
				Arguments.of(1L, null),
				Arguments.of(null, null));
		}

		@Test
		@DisplayName("주문할 상품 정보가 존재하지 않으면 BadRequest로 응답한다.")
		void failOrderWithNotContainOrderProductRequest() throws Exception {
			//given
			List<OrderProductRequestDto> orderProductsRequest = List.of();
			CreateOrderRequestDto requestDto = new CreateOrderRequestDto(address, zipCode, orderProductsRequest,
				location);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("배송정보중 주소가 기재되어있지 않으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		@ParameterizedTest(name = "{index}: address: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failOrderWithInvalidAddress(String invalidAddress) throws Exception {
			//given
			OrderProductRequestDto orderProductRequest = new OrderProductRequestDto(buyingProductId, 1L, null);
			var requestDto = new CreateOrderRequestDto(invalidAddress, zipCode, List.of(orderProductRequest), location);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("배송정보중 우편번호가 기재되어있지 않으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		@ParameterizedTest(name = "{index}: zipCode: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failOrderWithInvalidZipCode(String invalidZipCode) throws Exception {
			//given
			OrderProductRequestDto orderProductRequest = new OrderProductRequestDto(buyingProductId, 1L, null);
			var requestDto = new CreateOrderRequestDto(address, invalidZipCode, List.of(orderProductRequest), location);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("주문할 상품정보에 아무것도 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: productId: {0}, orderQuantity: {1}")
		@MethodSource("provideNullWithProductIdOrOrderQuantity")
		void failOrderWithInvalidOrderProduct(Long productId, Long orderQuantity) throws Exception {
			//given
			var orderProductsRequest = new OrderProductRequestDto(productId, orderQuantity, null);
			var requestDto = new CreateOrderRequestDto(address, zipCode, List.of(orderProductsRequest), location);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		ResultActions getPerform(CreateOrderRequestDto requestDto) throws Exception {
			return mockMvc.perform(
				post(URI_PREFIX)
					.content(objectMapper.writeValueAsString(requestDto))
					.contentType(APPLICATION_JSON)
			);
		}
	}

	@Test
	@DisplayName("나의 주문목록을 조회한다.")
	void testGetMyOrders() throws Exception {
		//given
		OrderPageRequestDto pageRequest = new OrderPageRequestDto(
			20L,
			10,
			null,
			null,
			null,
			null,
			null,
			null,
			null);

		var params = new ParameterUtils<OrderPageRequestDto>().toMultiValueParams(objectMapper, pageRequest);
		//when
		ResultActions perform = mockMvc.perform(
			get(URI_PREFIX)
				.params(params)
				.contentType(APPLICATION_JSON)
		);
		//then
		perform.andExpect(status().isOk());
		verify(orderService, times(1)).getMyOrders(any(), any());
	}

	@Nested
	@DisplayName("주문 목록을 조회할 때")
	class OrderPageRequestValidation {
		ParameterUtils<OrderPageRequestDto> paramUtils = new ParameterUtils<>();

		@Test
		@DisplayName("사이즈가 없다면 BadRequest로 응답한다")
		void failSizeWithNull() throws Exception {
			//given
			OrderPageRequestDto pageRequest = new OrderPageRequestDto(
				20L,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
			var params = paramUtils.toMultiValueParams(objectMapper, pageRequest);
			//when
			ResultActions perform = getPerform(params);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("최소 가격 조건이 음수이면 BadReqeust로 응답한다")
		void failMinimumWithNegative() throws Exception {
			//given
			OrderPageRequestDto pageRequest = new OrderPageRequestDto(
				null,
				10,
				-1L,
				null,
				null,
				null,
				null,
				null,
				null);
			var params = paramUtils.toMultiValueParams(objectMapper, pageRequest);
			//when
			ResultActions perform = getPerform(params);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("최대 가격 조건이 음수이면 BadReqeust로 응답한다")
		void failMaximumWithNegative() throws Exception {
			//given
			OrderPageRequestDto pageRequest = new OrderPageRequestDto(
				null,
				10,
				null,
				-1L,
				null,
				null,
				null,
				null,
				null);
			var params = paramUtils.toMultiValueParams(objectMapper, pageRequest);
			//when
			ResultActions perform = getPerform(params);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("상품 검색 이름이 2자리 미만이면 BadReqeust로 응답한다")
		void failProductNameWithLowerTwoLength() throws Exception {
			//given
			OrderPageRequestDto pageRequest = new OrderPageRequestDto(
				null,
				10,
				null,
				null,
				null,
				null,
				"가",
				null,
				null);
			var params = paramUtils.toMultiValueParams(objectMapper, pageRequest);
			//when
			ResultActions perform = getPerform(params);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@NotNull
		private ResultActions getPerform(MultiValueMap<String, String> params) throws Exception {
			return mockMvc.perform(
				get(URI_PREFIX)
					.params(params)
					.contentType(APPLICATION_JSON)
			);
		}
	}

	@Test
	@DisplayName("주문을 취소한다")
	void testCancel() throws Exception {
		//given
		Long orderProductId = 1L;
		//when
		ResultActions perform = mockMvc.perform(
			patch(URI_PREFIX + "/" + orderProductId)
				.contentType(APPLICATION_JSON)
		);
		//then
		perform.andExpect(status().isOk());
		verify(orderService, times(1)).cancel(any(), any());
	}

	@Test
	@DisplayName("주문을 취소할때 주문상품 식별자가 음수이면 BadRequest로 응답한다")
	void failCancelWithNegativeValue() throws Exception {
		//given
		Long invalidOrderProductId = -1L;
		//when
		ResultActions perform = mockMvc.perform(
			patch(URI_PREFIX + "/" + invalidOrderProductId)
				.contentType(APPLICATION_JSON)
		);
		//then
		perform.andExpect(status().isBadRequest());
	}
}