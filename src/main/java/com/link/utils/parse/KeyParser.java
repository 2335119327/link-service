package com.link.utils.parse;


import com.link.model.context.KeyParserContext;

/**
 * key 解析接口
 *
 * @author : wangaidong
 * @date : 2023/5/6 15:09
 */
public interface KeyParser {

	/**
	 * match by type
	 *
	 * @param type 处理器类型
	 * @return 匹配是否成功
	 */
	boolean isResponsibleFor(String type);

	/**
	 * 解析 key
	 *
	 * @param context key解析上下文
	 * @return 解析后的key
	 */
	String parse(KeyParserContext context);

}
