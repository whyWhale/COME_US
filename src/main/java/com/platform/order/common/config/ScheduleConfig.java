package com.platform.order.common.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@Profile({"local", "prod"})
@EnableSchedulerLock(defaultLockAtMostFor = "PT20S", defaultLockAtLeastFor = "PT20S")
@Configuration
@EnableScheduling
public class ScheduleConfig {
	@Bean
	public LockProvider lockProvider(
		@Qualifier("masterDataSource")
		DataSource dataSource
	) {
		return new JdbcTemplateLockProvider(dataSource);
	}

}
