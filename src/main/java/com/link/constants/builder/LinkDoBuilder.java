package com.link.constants.builder;

import com.link.constants.enums.LinkStatusEnum;
import com.link.model.entity.LinkDO;
import com.link.model.vo.create.GenerateShortLinkRequestVO;
import com.link.utils.DateTimeUtil;

import java.time.LocalDateTime;

/**
 * @author : wangaidong
 * @date : 2023/11/28 17:33
 */
public class LinkDoBuilder {

	public static LinkDO generateLinkDO(GenerateShortLinkRequestVO request, String shortLinkSuffix) {
		LinkDO linkDO = new LinkDO();
		linkDO.setLongLink(request.getLongLink());
		linkDO.setShortSuffix(shortLinkSuffix);
		linkDO.setStatus(LinkStatusEnum.AVAILABLE.getStatus());
		linkDO.setAddTime(LocalDateTime.now());
		linkDO.setExpireTime(DateTimeUtil.epochMilliToBeijingTime(request.getExpireTime()));
		return linkDO;
	}

}
