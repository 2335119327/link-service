package com.link.task;

import com.link.dao.LinkDAO;

import javax.annotation.Resource;

/**
 * @author : wangaidong
 * @date : 2023/11/29 13:13
 * @description : 清理过期link定时任务
 */
public class ClearExpireLinkTask {

	@Resource
	private LinkDAO linkDAO;

	public void clearExpireLinkTask() {

	}

}
