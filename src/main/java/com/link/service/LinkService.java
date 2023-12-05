package com.link.service;

import com.link.model.vo.delete.DeleteShortLinkRequestVO;
import com.link.model.vo.delete.DeleteShortLinkResponseVO;
import com.link.model.vo.create.GenerateShortLinkRequestVO;
import com.link.model.vo.create.GenerateShortLinkResponseVO;
import com.link.model.vo.update.UpdateLinkRequestVO;
import com.link.model.vo.update.UpdateLinkResponseVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : wangaidong
 * @date : 2023/11/28 14:43
 */
public interface LinkService {

	/**
	 * 生成短链
	 */
	GenerateShortLinkResponseVO generateShortLink(GenerateShortLinkRequestVO request);

	/**
	 * 查询短链对应的长链
	 */
	String getLongLinkByShortSuffix(String shortSuffix);

	/**
	 * 查询长链对应的短链
	 */
	String getShortSuffixByLongLink(String longLink);

	/**
	 * 删除短链
	 */
	DeleteShortLinkResponseVO deleteShortLink(DeleteShortLinkRequestVO request);

	/**
	 * http来源获取长链
	 */
	String getLongLinkByShortSuffixFromHttp(HttpServletRequest request, String shortSuffix);

	/**
	 * 更新link
	 */
	UpdateLinkResponseVO updateLink(UpdateLinkRequestVO request);

}
