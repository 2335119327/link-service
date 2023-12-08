package com.link.task;

import com.link.dao.LinkDAO;
import com.link.model.entity.LinkDO;
import com.link.service.LinkService;
import com.link.utils.DataUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : wangaidong
 * @date : 2023/11/29 13:13
 * @description : 清理过期link定时任务
 */
@Slf4j
@Component
public class ClearExpireLinkTask {

	@Resource
	private LinkDAO linkDAO;

	@Resource
	private LinkService linkService;

	@XxlJob("clearExpireLinkTask")
	public void clearExpireLinkTask() {
		try {
			long maxId = -1;
			while (true) {
				List<LinkDO> linkDOList = linkDAO.pageExpireLink(maxId,
						Integer.parseInt(DataUtil.getOrDefault(XxlJobHelper.getJobParam(), "500")),
						XxlJobHelper.getShardIndex(),
						XxlJobHelper.getShardTotal());
				if (CollectionUtils.isEmpty(linkDOList)) {
					return;
				}
				maxId = linkDOList.get(linkDOList.size() - 1).getId();

				// 删库记录
				linkDAO.deleteBatchIds(linkDOList.stream().map(LinkDO::getId).collect(Collectors.toList()));

				// 删缓存
				linkService.asyncBatchRemoveShortLinkCache(linkDOList);
			}
		} catch (Exception e) {
			log.error("clearExpireLinkTask error, jobId={}", XxlJobHelper.getJobId(),e);
			// todo 告警
		}
	}

}
