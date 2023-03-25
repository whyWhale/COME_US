package com.platform.order.product.domain.category.repository;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.platform.order.product.domain.category.entity.CategoryEntity;
import com.platform.order.testenv.RepositoryTest;

@Sql(scripts = "classpath:/load/category.sql")
class CategoryRepositoryTest extends RepositoryTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	EntityManagerFactory entityManagerFactory;

	@Test
	@DisplayName("카테고리 항목을 모두 가져오면 상위, 하위에 있는 카테고리들도 모드 로드 된다.")
	void testAllCategories() {
		//given
		//when
		List<CategoryEntity> categories = categoryRepository.findAllOrderByCode();
		//then
		categories.stream()
			.filter(categoryEntity -> categoryEntity.getParent().isEmpty())
			.forEach(categoryEntity -> {
					categoryEntity.getChilds().forEach(subCategory -> {
						boolean isLoadParent = entityManagerFactory.getPersistenceUnitUtil()
							.isLoaded(subCategory);
						Assertions.assertThat(isLoadParent).isTrue();
					});
				}
			);

		categories.stream()
			.filter(categoryEntity -> categoryEntity.getParent().isPresent())
			.forEach(categoryEntity -> {
					boolean isLoadParent = entityManagerFactory.getPersistenceUnitUtil()
						.isLoaded(categoryEntity.getParent().get());

					Assertions.assertThat(isLoadParent).isTrue();
				}
			);
	}

}