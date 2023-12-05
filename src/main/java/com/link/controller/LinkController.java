package com.link.controller;

import com.link.model.vo.delete.DeleteShortLinkRequestVO;
import com.link.model.vo.delete.DeleteShortLinkResponseVO;
import com.link.model.vo.create.GenerateShortLinkRequestVO;
import com.link.model.vo.create.GenerateShortLinkResponseVO;
import com.link.model.vo.update.UpdateLinkRequestVO;
import com.link.model.vo.update.UpdateLinkResponseVO;
import com.link.service.LinkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : wangaidong
 * @date : 2023/11/28 14:41
 */
@Slf4j
@RestController
public class LinkController {

	@Resource
	private LinkService linkService;

	/**
	 * 生成短链
	 */
	@PostMapping("/link/generateShortLink")
	public GenerateShortLinkResponseVO generateShortLink(@RequestBody GenerateShortLinkRequestVO request) {
		return linkService.generateShortLink(request);
	}

	/**
	 * 删除短链
	 */
	@PostMapping("/link/deleteShortLink")
	public DeleteShortLinkResponseVO deleteShortLink(@RequestBody DeleteShortLinkRequestVO request) {
		return linkService.deleteShortLink(request);
	}

	/**
	 * 更新
	 */
	@PostMapping("/link/updateLink")
	public UpdateLinkResponseVO updateLink(@RequestBody UpdateLinkRequestVO request) {
		return linkService.updateLink(request);
	}

	/**
	 * 访问短链，查询长链并重定向
	 */
	@GetMapping("/{shortSuffix}")
	public void redirect(@PathVariable String shortSuffix, HttpServletRequest request, HttpServletResponse response) {
		log.info("link redirect, shortSuffix={}, user-agent={}, ip={}", shortSuffix, request.getHeader("user-agent"), request.getRemoteAddr());

		// 查询对应长链
		String longLink = StringUtils.isBlank(shortSuffix) ? null : linkService.getLongLinkByShortSuffixFromHttp(request, shortSuffix);
		if (StringUtils.isNotBlank(longLink)) {
			try {
				response.sendRedirect(longLink);
			} catch (Exception e) {
				log.error("LinkController redirect error, shortSuffix={}", shortSuffix, e);
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			return;
		}

		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

}
