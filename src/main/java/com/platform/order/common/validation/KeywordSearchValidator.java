package com.platform.order.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
