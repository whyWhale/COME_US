package com.platform.order.env;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.platform.order.config.TestJpaAuditConfig;
import com.platform.order.config.TestQueryDslConfig;

@Import({TestQueryDslConfig.class, TestJpaAuditConfig.class})
@DataJpaTest(showSql = false)
public class RepositoryTest {
}
