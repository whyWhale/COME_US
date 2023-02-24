package com.platform.order.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.order.common.config.WebSecurityConfig;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtConfig;
import com.platform.order.common.security.service.TokenService;
import com.platform.order.product.controller.dto.request.CreateProductRequestDto;
import com.platform.order.product.controller.dto.request.ProductPageRequestDto;
import com.platform.order.product.controller.dto.request.ProductPageRequestDto.ProductCondition;
import com.platform.order.product.controller.dto.request.UpdateProductRequestDto;
import com.platform.order.product.service.ProductService;
import com.platform.order.security.WithJwtMockUser;
import com.platform.order.utils.ParameterUtils;

@WithJwtMockUser
@WebMvcTest({ProductController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtConfig.class})
class ProductControllerTest {

	final String URI_PREFIX = "/api/products";
	final Long productId = 1L;
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	TokenService tokenService;

	@MockBean
	ProductService productService;

	@Test
	@DisplayName("상품을 생성한다")
	void testCreate() throws Exception {
		//given
		CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, 1000L, "C001");
		String body = objectMapper.writeValueAsString(requestDto);
		//when
		ResultActions perform = mockMvc.perform(post(URI_PREFIX)
			.content(body)
			.contentType(MediaType.APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(productService, times(1)).create(1L, requestDto);
	}

	@DisplayName("상품을 생성할 때")
	@Nested
	class CreateProductValidation {

		@DisplayName("이름이 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: name: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failCreateWithBlankName(String name) throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto(name, 10L, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("수량이 음수이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: quantity: {0}")
		@ValueSource(longs = {-1, -12})
		void failCreateWithNegativeQuantity(Long quantity) throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", quantity, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("수량이 없으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		void failCreateWithNullQuantity() throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", null, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("가격이 음수이거나 0이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: price: {0}")
		@ValueSource(longs = {-1, 0})
		void failCreateWithNegativePrice(Long price) throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, price, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("가격이 없으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		void failCreateWithNullPrice() throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, null, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("카테고리 코드가 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: categoryCode: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failCreateWithBlankCategoryCode(String categoryCode) throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, 1000L, categoryCode);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		ResultActions getPerform(CreateProductRequestDto requestDto) throws Exception {
			return mockMvc.perform(
				post(URI_PREFIX)
					.content(objectMapper.writeValueAsString(requestDto))
					.contentType(APPLICATION_JSON)
			);
		}
	}

	@Test
	@DisplayName("상품을 수정한다.")
	void testUpdate() throws Exception {
		//given
		UpdateProductRequestDto requestDto = new UpdateProductRequestDto("updating product", 10000L, 1000L,
			"C032");
		//when
		ResultActions perform = mockMvc.perform(
			patch(URI_PREFIX + "/" + productId)
				.content(objectMapper.writeValueAsString(requestDto))
				.contentType(APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(productService, times(1)).update(1L, productId, requestDto);
	}

	@DisplayName("상품을 수정할 때 ")
	@Nested
	class UpdateProductValidation {

		@DisplayName("이름이 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: name: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failUpdateWithBlankName(String name) throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto(name, 10L, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("수량이 음수이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: quantity: {0}")
		@ValueSource(longs = {-1, -1000})
		void failUpdateWithNegativeQuantity(Long quantity) throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", quantity, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("수량이 없으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		void failUpdateWithNullQuantity() throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", null, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("가격이 음수이거나 0이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: price: {0}")
		@ValueSource(longs = {-1, 0})
		void failUpdateWithNegativePrice(Long price) throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", 10L, price, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("가격이 없으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		void failUpdateWithNullPrice() throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", 10L, null, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("카테고리 코드가 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: categoryCode: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failUpdateWithBlankCategoryCode(String categoryCode) throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", 10L, 1000L, categoryCode);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		ResultActions getPerform(UpdateProductRequestDto requestDto) throws Exception {
			return mockMvc.perform(
				patch(URI_PREFIX + "/" + productId)
					.content(objectMapper.writeValueAsString(requestDto))
					.contentType(APPLICATION_JSON)
			);
		}
	}

	@Test
	@DisplayName("상품을 삭제한다.")
	void testDelete() throws Exception {
		//given
		//when
		ResultActions perform = mockMvc.perform(delete(URI_PREFIX + "/" + productId)
			.contentType(APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(productService, times(1)).delete(productId, 1L);
	}

	@Test
	@DisplayName("상품 상세를 확인한다.")
	void testRead() throws Exception {
		//given
		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX + "/" + productId)
			.contentType(APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(productService, times(1)).read(productId);
	}

	@Test
	@DisplayName("전체 상품들을 조회한다.")
	void testReadAll() throws Exception {
		//given
		ProductPageRequestDto productPageRequest = new ProductPageRequestDto(1, 10, null, null, null,
			null);
		MultiValueMap<String, String> params = new ParameterUtils<ProductPageRequestDto>().toMultiValueParams(
			objectMapper, productPageRequest);

		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX).params(params)
			.contentType(APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(productService, times(1)).readAll(any());
	}

	@Nested
	@DisplayName("전체 상품을 조회할 때")
	class ReadAllProductValidation {
		static Stream<Arguments> getMinAndMaxPrice() {
			return Stream.of(
				Arguments.arguments(-1L, 10L),
				Arguments.arguments(10L, -1L),
				Arguments.arguments(-1L, -1L)
			);
		}

		@Test
		@DisplayName("이름 검색이 1자이면 BadRequest로 응답한다")
		void failReadAllWithInvalidName() throws Exception {
			//given
			ProductPageRequestDto productPageRequest = new ProductPageRequestDto(1, 10, "아", null, null,
				null);
			MultiValueMap<String, String> params = new ParameterUtils<ProductPageRequestDto>().toMultiValueParams(
				objectMapper, productPageRequest);
			//when
			ResultActions perform = getPerform(productPageRequest, params);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("하한 금액 또는 상한 금액이 음수이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: min : {0}, max : {1}")
		@MethodSource("getMinAndMaxPrice")
		void failReadAllWithInvalidPrice(Long minPrice, Long maxPrice) throws Exception {
			//given
			ProductPageRequestDto productPageRequest = new ProductPageRequestDto(1, 10, null, maxPrice,
				minPrice,
				null);
			MultiValueMap<String, String> params = new ParameterUtils<ProductPageRequestDto>().toMultiValueParams(
				objectMapper, productPageRequest);
			//when
			ResultActions perform = getPerform(productPageRequest, params);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("정렬 조건이 2개가 넘어가면 BadRequest로 응답한다.")
		void failReadAllWithInvalidOrdering() throws Exception {
			//given
			ProductPageRequestDto productPageRequest = new ProductPageRequestDto(1, 10, null, null, null,
				null);
			MultiValueMap<String, String> params = new ParameterUtils<ProductPageRequestDto>().toMultiValueParams(
				objectMapper, productPageRequest);
			//when
			ResultActions perform = mockMvc.perform(
				get(URI_PREFIX).params(params)
					.param("sorts", ProductCondition.PRICE_ASC.name())
					.param("sorts", ProductCondition.CREATED_ASC.name())
					.param("sorts", ProductCondition.PRICE_DESC.name())
					.contentType(APPLICATION_JSON));
			//then
			perform.andExpect(status().isBadRequest());
		}

		ResultActions getPerform(ProductPageRequestDto requestDto, MultiValueMap<String, String> params) throws
			Exception {
			return mockMvc.perform(
				get(URI_PREFIX).params(params)
					.contentType(APPLICATION_JSON));
		}
	}
}