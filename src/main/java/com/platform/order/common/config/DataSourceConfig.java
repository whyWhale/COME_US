package com.platform.order.common.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

@Profile({"local", "prod"})
@RequiredArgsConstructor
@Configuration
public class DataSourceConfig {

	private final String MASTER_DATASOURCE = "masterDataSource";
	private final String SLAVE_DATASOURCE = "slaveDataSource";
	private final String ROUTING_DATASOURCE = "routingDataSource";

	@Bean(MASTER_DATASOURCE)
	@ConfigurationProperties(prefix = "spring.datasource.master.hikari")
	public DataSource getMasterDataSource() {
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(SLAVE_DATASOURCE)
	@ConfigurationProperties(prefix = "spring.datasource.slave.hikari")
	public DataSource getSlaveDataSource() {
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean
	@DependsOn({MASTER_DATASOURCE, SLAVE_DATASOURCE})
	public DataSource routingDataSource(
		@Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
		@Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource
	) {

		RoutingDataSource routingDataSource = new RoutingDataSource();

		HashMap<Object, Object> dataSources = new HashMap<>();

		dataSources.put("master", masterDataSource);
		dataSources.put("slave", slaveDataSource);

		routingDataSource.setTargetDataSources(dataSources);
		routingDataSource.setDefaultTargetDataSource(masterDataSource);

		return routingDataSource;
	}

	@Primary
	@Bean
	@DependsOn({ROUTING_DATASOURCE})
	public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
		return new LazyConnectionDataSourceProxy(routingDataSource);
	}

}
