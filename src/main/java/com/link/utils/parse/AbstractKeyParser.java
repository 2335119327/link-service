package com.link.utils.parse;


import com.link.model.context.KeyParserContext;
import org.apache.commons.lang3.StringUtils;

/**
 * 抽象 key解析器
 *
 * @author : wangaidong
 * @date : 2023/5/6 15:13
 */
public abstract class AbstractKeyParser implements KeyParser {

	private final String parseType;

	public AbstractKeyParser(String parseType) {
		this.parseType = parseType;
	}

	@Override
	public boolean isResponsibleFor(String type) {
		return StringUtils.equals(parseType, type);
	}

	@Override
	public String parse(KeyParserContext context) {
		return context.getKey();
	}

}
