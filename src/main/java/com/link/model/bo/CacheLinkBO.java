package com.link.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author : wangaidong
 * @date : 2023/11/28 20:25
 */
@Data
public class CacheLinkBO {

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
