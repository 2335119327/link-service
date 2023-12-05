package com.link.model.vo.delete;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : wangaidong
 * @date : 2023/11/30 14:33
 */
@Data
public class DeleteShortLinkResponseVO implements Serializable {

	/**
	 * 是否删除成功
	 */
	private Boolean success = true;

}
