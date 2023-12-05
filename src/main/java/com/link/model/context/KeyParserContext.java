package com.link.model.context;

import lombok.Data;
import org.aspectj.lang.JoinPoint;

/**
 * @author : wangaidong
 * @date : 2023/12/4 17:05
 */
@Data
public class KeyParserContext {

	/**
	 * method 连接点
	 */
	private JoinPoint joinPoint;

	/**
	 * 解析类型
	 */
	private String parserType;

	private String key;

	public static KeyParserContext prepareKeyParserContext(String key, String parserType, JoinPoint joinPoint) {
		KeyParserContext keyParserContext = new KeyParserContext();
		keyParserContext.setParserType(parserType);
		keyParserContext.setJoinPoint(joinPoint);
		keyParserContext.setKey(key);
		return keyParserContext;
	}

}
