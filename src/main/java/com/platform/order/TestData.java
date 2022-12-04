package com.platform.order;

import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.auth.domain.entity.Role;
import com.platform.order.auth.usecase.UserService;
import com.platform.order.auth.view.dto.UserDto;

@Profile("local")
@Component
public class TestData {

	private final InitService initService;

	public TestData(InitService initService) {
		this.initService = initService;
	}

	@PostConstruct
	public void init() {
		initService.init();
	}

	@Transactional
	@Component
	static class InitService {
		private final UserService userService;

		public InitService(UserService userService) {
			this.userService = userService;
		}

		public void init() {
			String username = "whyWhale";
			String password = "1";
			String nickName = username;
			String email = username + "@ooo.co.kr";
			Role role = Role.USER;

			IntStream.rangeClosed(1, 10)
				.forEach(value -> {
					userService.save(new UserDto.SignUpRequest(
						username+String.valueOf(value),
						password,
						nickName+String.valueOf(value),
						email,
						role
					));
				});
		}
	}

}
