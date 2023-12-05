package com.link.utils.parse.handler;

import com.link.model.context.KeyParserContext;
import com.link.utils.parse.AbstractKeyParser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


/**
 * spel解析器
 *
 * @author : wangaidong
 * @date : 2023/5/23 10:06
 */
public class SpelExpressionParserHandler extends AbstractKeyParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpelExpressionParserHandler.class);

	private static final String parseType = "spel";

	/**
	 * 用于SpEL表达式解析.
	 */
	private static SpelExpressionParser SPEL_PARSER = new SpelExpressionParser();

	/**
	 * 获取方法参数定义名字
	 */
	private static DefaultParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

	public SpelExpressionParserHandler() {
		super(parseType);
	}

	@Override
	public String parse(KeyParserContext context) {
		JoinPoint joinPoint = context.getJoinPoint();
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

		// 获取方法参数名称
		String[] parameterNames = NAME_DISCOVERER.getParameterNames(methodSignature.getMethod());
		if (parameterNames == null || parameterNames.length == 0) {
			LOGGER.warn("method param is empty, return key, parserType={}, key={}", context.getParserType(), context.getKey());
			return context.getKey();
		}

		StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
		Object[] args = joinPoint.getArgs();
		for (int i = 0; i < args.length; i++) {
			standardEvaluationContext.setVariable(parameterNames[i], args[i]);
		}

		// 解析key
		Expression expression = SPEL_PARSER.parseExpression(context.getKey());

		return String.valueOf(expression.getValue(standardEvaluationContext));
	}

}
