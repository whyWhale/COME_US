package com.platform.order.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KeywordSearchValidator.class)
public @interface KeywordSearch {
	String message() default "2단어 이상이여야 합니다.";

	Class[] groups() default {};

	Class[] payload() default {};
}
