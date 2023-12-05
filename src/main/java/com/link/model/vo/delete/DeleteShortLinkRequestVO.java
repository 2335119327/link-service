package com.link.model.vo.delete;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : wangaidong
 * @date : 2023/11/30 14:33
 */
@Data
public class DeleteShortLinkRequestVO implements Serializable {

	/**
	 * 需要删除的短链集合
	 */
	private List<String> shortLinkList;

}
