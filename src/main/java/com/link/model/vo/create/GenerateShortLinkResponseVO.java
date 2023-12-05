package com.link.model.vo.create;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : wangaidong
 * @date : 2023/11/28 15:40
 */
@Data
public class GenerateShortLinkResponseVO implements Serializable {

	/**
	 * 短链
	 */
	private String shortLink;

}
