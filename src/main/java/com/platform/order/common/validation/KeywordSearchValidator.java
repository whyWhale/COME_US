package com.platform.order.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * keyword 검색을 할때 첫문자 와일드카드를 방지하기 하기 위한 validation
 */
public class KeywordSearchValidator implements ConstraintValidator<KeywordSearch, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.equals("")) {
			return true;
		}

		if (value.isBlank() || value.length() <= 2) {
			return false;
		}

		return true;
	}
}
