package com.platform.order.coupon.domain.repository;

import static com.platform.order.coupon.controller.dto.request.UserCouponPageRequestDto.UserCouponCondition.ISSUED_DESC;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.coupon.controller.dto.request.UserCouponPageRequestDto;
import com.platform.order.coupon.domain.entity.CouponEntity;
import com.platform.order.coupon.domain.entity.CouponType;
import com.platform.order.coupon.domain.entity.UserCouponEntity;
import com.platform.order.testenv.RepositoryTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

class CustomUserCouponRepositoryImplTest extends RepositoryTest {

	@Autowired
	UserCouponRepository userCouponRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CouponRepository couponRepository;

	UserEntity user;
	List<CouponEntity> coupons;
	List<UserCouponEntity> userCoupons;

	@BeforeEach
	public void setUp() {
		List<CouponEntity> coupons = LongStream.rangeClosed(1, 55)
			.mapToObj(value -> CouponEntity.builder()
				.type(CouponType.FIXED)
				.quantity(100L)
				.amount(10000L)
				.expiredAt(LocalDate.now().plusMonths(1))
				.build())
			.toList();

		this.coupons = couponRepository.saveAll(coupons);

		user = userRepository.save(UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.USER)
			.build());

		userCoupons = userCouponRepository.saveAll(IntStream.range(0, 55)
			.mapToObj(value -> UserCouponEntity.builder()
				.user(user)
				.coupon(coupons.get(value))
				.issuedAt(LocalDate.now())
				.build())
			.toList());
	}

	@AfterEach
	public void setDown() {
		userCouponRepository.deleteAllInBatch();
		couponRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("쿠폰 55개를 보유하고 있는 사용자가 사용가능한 쿠폰 목록을 조회한다.")
	void testFindAllUsableCoupons() {
		//given
		int page = 2;
		int size = 10;
		int expectedTotalPage = (int)Math.ceil((double)userCoupons.size() / size);

		var pageRequestDto = new UserCouponPageRequestDto(page, size, null, null, null, null, true, null,
			List.of(ISSUED_DESC));
		//when
		var userCouponPage = userCouponRepository.findAllWithConditions(pageRequestDto, user.getId());
		//then
		assertThat(userCouponPage.getTotalPages()).isEqualTo(expectedTotalPage);
		assertThat(userCouponPage.getPageable().getPageNumber()).isEqualTo(page);
		assertThat(userCouponPage.getPageable().getPageSize()).isEqualTo(size);
	}

	@Test
	@DisplayName("쿠폰 55개를 보유하고 있는 사용자가 이미 사용한 쿠폰 목록을 조회한다.")
	void testFindAllWithNotUsableCoupons() {
		//given
		List<UserCouponEntity> notUsableUserCoupons = userCouponRepository.saveAll(IntStream.range(0, 3)
			.mapToObj(value -> UserCouponEntity.builder()
				.user(user)
				.coupon(coupons.get(value))
				.issuedAt(LocalDate.now().minusDays(value))
				.isUsable(false)
				.build())
			.toList());

		int page = 1;
		int size = 10;
		int expectedTotalPage = (int)Math.ceil((double)notUsableUserCoupons.size() / size);

		var pageRequestDto = new UserCouponPageRequestDto(page, size, null, null, null, null, false, null,
			List.of(ISSUED_DESC));
		//when
		var userCouponPage = userCouponRepository.findAllWithConditions(pageRequestDto, user.getId());
		//then
		assertThat(userCouponPage.getTotalPages()).isEqualTo(expectedTotalPage);
		assertThat(userCouponPage.getPageable().getPageNumber()).isEqualTo(page);
		assertThat(userCouponPage.getPageable().getPageSize()).isEqualTo(size);
		userCouponPage.getContent().forEach(userCouponEntity -> assertThat(userCouponEntity.isUsable()).isFalse());
	}
}