package com.link.constants.enums;

import lombok.Getter;

/**
 * @author : wangaidong
 * @date : 2023/11/28 15:34
 */
@Getter
public enum LinkStatusEnum {

	AVAILABLE(1, "可用"),
	NOT_AVAILABLE(0, "不可用");

	LinkStatusEnum(Integer status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 描述
	 */
	private String desc;

}
