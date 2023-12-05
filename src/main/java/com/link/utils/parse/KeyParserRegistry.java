package com.link.utils.parse;

import com.link.model.context.KeyParserContext;
import com.link.utils.ServiceLoaderUtil;
import com.link.utils.parse.handler.NoneParserHandler;
import com.link.utils.parse.handler.SpelExpressionParserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wangaidong
 * @date : 2023/5/19 18:07
 * @description :
 */
public class KeyParserRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(KeyParserRegistry.class);

	private static final List<KeyParser> keyParserList;

	static {
		keyParserList = new ArrayList<>(4);
		keyParserList.add(new NoneParserHandler());
		keyParserList.add(new SpelExpressionParserHandler());
		keyParserList.addAll(ServiceLoaderUtil.load(KeyParser.class));
	}

	public static String parser(KeyParserContext context) {
		// 匹配key解析器
		KeyParser keyParser = matchKeyParser(context);
		LOGGER.info("KeyParserRegistry#parser parserType={}, key={}", context.getParserType(), context.getKey());

		return keyParser.parse(context);
	}

	public static KeyParser matchKeyParser(KeyParserContext context) {
		for (KeyParser keyParser : keyParserList) {
			if (!keyParser.isResponsibleFor(context.getParserType())) {
				continue;
			}

			return keyParser;
		}

		throw new RuntimeException("match KeyParserHandler is empty");
	}

}
