package com.link.utils.convert;

import com.link.model.entity.LinkDO;
import com.link.model.bo.CacheLinkBO;
import com.link.model.vo.LinkVO;

import java.time.LocalDateTime;

/**
 * @author : wangaidong
 * @date : 2023/11/29 19:34
 * @description : convert
 */
public class ConvertUtil {

	public static CacheLinkBO linkDoConvertToCacheLinkBo(LinkDO linkDO) {
		CacheLinkBO cacheLinkBO = new CacheLinkBO();
		cacheLinkBO.setShortSuffix(linkDO.getShortSuffix());
		cacheLinkBO.setLongLink(linkDO.getLongLink());
		cacheLinkBO.setExpireTime(linkDO.getExpireTime());
		return cacheLinkBO;
	}

	public static LinkDO linkVoConvertToLinDo(LinkVO linkVO) {
		LinkDO linkDO = new LinkDO();
		linkDO.setStatus(linkVO.getStatus());
		linkDO.setExpireTime(linkVO.getExpireTime());
		linkDO.setLongLink(linkVO.getLongLink());
		linkDO.setShortSuffix(linkVO.getShortSuffix());
		return linkDO;
	}

}
