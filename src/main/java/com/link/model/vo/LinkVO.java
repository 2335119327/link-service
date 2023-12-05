package com.link.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : wangaidong
 * @date : 2023/12/5 15:37
 */
@Data
public class LinkVO implements Serializable {

	/**
	 * 状态（1: 可用，0: 不可用）
	 */
	private Integer status;

	/**
	 * 短链
	 */
	private String shortSuffix;

	/**
	 * 长链
	 */
	private String longLink;

	/**
	 * 过期时间
	 */
	private LocalDateTime expireTime;

}
