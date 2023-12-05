package com.link.utils.parse.handler;


import com.link.utils.parse.AbstractKeyParser;

/**
 * @author : wangaidong
 * @date : 2023/5/6 15:13
 * @description : 不做任何解析,保留原滋原味~
 */
public class NoneParserHandler extends AbstractKeyParser {

	private static final String PARSER_TYPE = "none";

	public NoneParserHandler() {
		super(PARSER_TYPE);
	}

}
