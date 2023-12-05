package com.link.model.vo.create;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : wangaidong
 * @date : 2023/11/28 15:40
 */
@Data
public class GenerateShortLinkRequestVO implements Serializable {

	/**
	 * 域名
	 */
	private String domainName;

	/**
	 * 长链
	 */
	private String longLink;

	/**
	 * 是否每次创建新链接
	 */
	private Boolean alwaysNewCreate;

	/**
	 * 过期时间(毫秒级时间戳)
	 */
	private Long expireTime;

}
