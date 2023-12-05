package com.link.model.vo.update;

import com.link.model.vo.LinkVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : wangaidong
 * @date : 2023/11/30 14:33
 */
@Data
public class UpdateLinkRequestVO implements Serializable {

	/**
	 * 待更新link集合
	 */
	private List<LinkVO> updateLinkList;

}
