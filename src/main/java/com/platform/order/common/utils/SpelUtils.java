package com.platform.order.common.utils;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelUtils {
	/**
	 * RedLock Annotation 식별할 수 있는 키값을 SPEL로 읽어와 파싱하는 역할
	 * @param parameterNames
	 * @param args
	 * @param key
	 * @return
	 */
	public static String getDynamicValue(String[] parameterNames, Object[] args, String key) {
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();

		for (int i = 0; i < parameterNames.length; i++) {
			context.setVariable(parameterNames[i], args[i]);
		}

		return parser.parseExpression(key).getValue(context, String.class);
	}
}
