package com.link.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.link.config.annotation.AccessLimit;
import com.link.config.annotation.BlackAccessLimit;
import com.link.constants.builder.LinkDoBuilder;
import com.link.constants.constant.LinkConstant;
import com.link.constants.enums.LinkStatusEnum;
import com.link.constants.lambda.LinkLambda;
import com.link.dao.LinkDAO;
import com.link.model.entity.LinkDO;
import com.link.model.bo.CacheLinkBO;
import com.link.model.vo.LinkVO;
import com.link.model.vo.delete.DeleteShortLinkRequestVO;
import com.link.model.vo.delete.DeleteShortLinkResponseVO;
import com.link.model.vo.create.GenerateShortLinkRequestVO;
import com.link.model.vo.create.GenerateShortLinkResponseVO;
import com.link.model.vo.update.UpdateLinkRequestVO;
import com.link.model.vo.update.UpdateLinkResponseVO;
import com.link.service.LinkService;
import com.link.utils.LinkAssert;
import com.link.utils.collection.ListUtil;
import com.link.utils.Pair;
import com.link.utils.ShortLinkUtil;
import com.link.utils.convert.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * LinkService
 *
 * @author : wangaidong
 * @date : 2023/11/28 14:43
 */
@Slf4j
@Service
public class LinkServiceImpl implements LinkService {

	@Resource
	private LinkDAO linkDAO;

	@Resource
	private ShortLinkUtil shortLinkUtil;

	@Resource
	private Cache<String, CacheLinkBO> shortSuffixCache;

	@Resource
	private Cache<String, CacheLinkBO> longLinkCache;

	@Resource
	private RedissonClient redissonClient;

	@Resource
	private ThreadPoolTaskExecutor bizThreadPool;

	@Resource
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	@Override
	public GenerateShortLinkResponseVO generateShortLink(GenerateShortLinkRequestVO request) {
		log.info("generateShortLink request={}", JSONObject.toJSONString(request));

		// 预处理请求
		prepareProcessRequest(request);

		// response
		GenerateShortLinkResponseVO response = new GenerateShortLinkResponseVO();

		// 查询长链是否存在对应的短链
		String shortSuffix = request.getAlwaysNewCreate() ? null : getShortSuffixByLongLink(request.getLongLink());
		if (StringUtils.isNotBlank(shortSuffix)) {
			response.setShortLink(request.getDomainName() + shortSuffix);
			return response;
		}

		// 生成短链
		String shortLinkSuffix = shortLinkUtil.generateShortSuffixAndRetry();
		LinkAssert.notEmpty(shortLinkSuffix, "generate shortLink fail");

		// 建立短链、长链映射并入库
		LinkDO linkDO = LinkDoBuilder.generateLinkDO(request, shortLinkSuffix);
		linkDAO.insert(linkDO);

		// 异步写缓存
		asyncWriteCache(linkDO);

		response.setShortLink(request.getDomainName() + shortLinkSuffix);
		log.info("generateShortLink response={}", JSONObject.toJSONString(response));
		return response;
	}

	private void prepareProcessRequest(GenerateShortLinkRequestVO request) {
		LinkAssert.notEmpty(request, "request is empty");
		LinkAssert.notEmpty(request.getLongLink(), "longLink is empty");
		LinkAssert.isTrue(!request.getLongLink().contains(" "), "longLink illegal");
		LinkAssert.isTrue(request.getExpireTime() != null && request.getExpireTime() > System.currentTimeMillis(), "expireTime illegal");

		if (request.getAlwaysNewCreate() == null) {
			request.setAlwaysNewCreate(false);
		}

		// todo 本地测试用的
		if (StringUtils.isBlank(request.getDomainName())) {
			request.setDomainName(LinkConstant.LOCAL_ADDRESS);
		}
	}

	private void asyncWriteCache(LinkDO linkDO) {
		CompletableFuture.runAsync(() -> {
			if (StringUtils.isNotBlank(linkDO.getShortSuffix())) {
				String shortLinkKey = LinkLambda.SHORT_LINK_KEY_APPEND.apply(linkDO.getShortSuffix());
				// 写local缓存
				LinkLambda.LOCAL_CACHE_SET.accept(shortSuffixCache, shortLinkKey, linkDO);
				// 写redis
				LinkLambda.REDIS_CACHE_SET.accept(redissonClient, shortLinkKey, linkDO);
			}

			if (StringUtils.isNotBlank(linkDO.getLongLink())) {
				String longLinkKey = LinkLambda.LONG_LINK_KEY_APPEND.apply(linkDO.getLongLink());
				// 写local缓存
				LinkLambda.LOCAL_CACHE_SET.accept(longLinkCache, longLinkKey, linkDO);
				// 写redis
				LinkLambda.REDIS_CACHE_SET.accept(redissonClient, longLinkKey, linkDO);
			}
		}, bizThreadPool).exceptionally(t -> {
			log.error("asyncWriteCache error, linkDO={}", JSONObject.toJSONString(linkDO));
			return null;
		});
	}

	@Override
	public String getLongLinkByShortSuffix(String shortSuffix) {
		// 拼接key
		String cacheKey = LinkLambda.SHORT_LINK_KEY_APPEND.apply(shortSuffix);

		// 查询本地缓存
		CacheLinkBO localCacheLink = LinkLambda.LOCAL_CACHE_GET.apply(shortSuffixCache, cacheKey);
		if (localCacheLink != null) {
			return localCacheLink.getLongLink();
		}

		// 查询redis
		CacheLinkBO redisCacheLink = LinkLambda.REDIS_CACHE_GET.apply(redissonClient, shortSuffixCache, cacheKey);
		if (redisCacheLink != null) {
			return redisCacheLink.getLongLink();
		}

		// 查库, 一个短链对应一个长链
		LinkDO linkDO = linkDAO.selectOne(new LambdaQueryWrapper<LinkDO>()
				.select(LinkDO::getShortSuffix, LinkDO::getLongLink, LinkDO::getExpireTime, LinkDO::getId)
				.eq(LinkDO::getStatus, LinkStatusEnum.AVAILABLE.getStatus())
				.eq(LinkDO::getShortSuffix, shortSuffix));
		return postProcessDataBaseData(linkDO).getRight();
	}

	@AccessLimit(limitKey = "#request.remoteAddr", cycle = 60, frequency = 4,
			blackLimit = @BlackAccessLimit(blackKeyPrefix = "black:", cycle = 60, frequency = 8))
	@Override
	public String getLongLinkByShortSuffixFromHttp(HttpServletRequest request, String shortSuffix) {
		return getLongLinkByShortSuffix(shortSuffix);
	}

	@Override
	public String getShortSuffixByLongLink(String longLink) {
		// 拼接key
		String cacheKey = LinkLambda.LONG_LINK_KEY_APPEND.apply(longLink);

		// 查询本地缓存
		CacheLinkBO cacheLink = LinkLambda.LOCAL_CACHE_GET.apply(longLinkCache, cacheKey);
		if (cacheLink != null) {
			return cacheLink.getShortSuffix();
		}

		// 查询redis
		CacheLinkBO redisCacheLink = LinkLambda.REDIS_CACHE_GET.apply(redissonClient, longLinkCache, cacheKey);
		if (redisCacheLink != null) {
			return redisCacheLink.getShortSuffix();
		}

		// 查库, 一个长链可能对应多个短链
		LinkDO linkDO = linkDAO.selectOne(new LambdaQueryWrapper<LinkDO>()
				.select(LinkDO::getShortSuffix, LinkDO::getLongLink, LinkDO::getExpireTime, LinkDO::getId)
				.eq(LinkDO::getStatus, LinkStatusEnum.AVAILABLE.getStatus())
				.eq(LinkDO::getLongLink, longLink).last("limit 1"));
		return postProcessDataBaseData(linkDO).getLeft();
	}

	/**
	 * 后置处理数据库数据
	 *
	 * @param linkDO ling数据
	 * @return 左边短链，右边长链
	 */
	private Pair<String, String> postProcessDataBaseData(LinkDO linkDO) {
		if (linkDO == null) {
			return Pair.of(null, null);
		}

		LocalDateTime expireTime = linkDO.getExpireTime();
		if (expireTime != null && expireTime.isBefore(LocalDateTime.now())) {
			// 存在但是已过期，需要删除
			linkDAO.deleteById(linkDO.getId());

			// 异步删缓存
			asyncRemoveSingleCache(linkDO);
			return Pair.of(null, null);
		}

		// 走到这，说明本地缓存和redis都没有命中，所需这里需要写入双缓存
		asyncWriteCache(linkDO);

		return Pair.of(linkDO.getShortSuffix(), linkDO.getLongLink());
	}

	private void asyncRemoveSingleCache(LinkDO linkDO) {
		CompletableFuture.runAsync(() -> {
			// 删短链缓存
			String shortSuffixKey = LinkLambda.SHORT_LINK_KEY_APPEND.apply(linkDO.getShortSuffix());
			shortSuffixCache.invalidate(shortSuffixKey);
			RBucket<String> shortSuffixBucket = redissonClient.getBucket(shortSuffixKey);
			if (shortSuffixBucket.isExists() || shortSuffixBucket.remainTimeToLive() > 0) {
				shortSuffixBucket.delete();
			}

			// 删长链缓存
			String longLinkKey = LinkLambda.LONG_LINK_KEY_APPEND.apply(linkDO.getLongLink());
			longLinkCache.invalidate(longLinkKey);
			RBucket<String> longLinkBucket = redissonClient.getBucket(longLinkKey);
			if (longLinkBucket.isExists() || longLinkBucket.remainTimeToLive() > 0) {
				longLinkBucket.delete();
			}
		}, bizThreadPool).exceptionally(t -> {
			log.error("asyncRemoveCache error, linkDO={}", JSONObject.toJSONString(linkDO), t);
			return null;
		});
	}

	@Override
	public DeleteShortLinkResponseVO deleteShortLink(DeleteShortLinkRequestVO request) {
		LinkAssert.notEmpty(request, "request is empty");
		LinkAssert.isTrue(CollectionUtils.isNotEmpty(request.getShortLinkList()), "shortLinkList is empty");

		DeleteShortLinkResponseVO response = new DeleteShortLinkResponseVO();
		try {
			// 先查库
			List<LinkDO> linkDOList = linkDAO.selectList(new LambdaQueryWrapper<LinkDO>()
					.select(LinkDO::getLongLink, LinkDO::getShortSuffix, LinkDO::getId)
					.in(LinkDO::getShortSuffix, request.getShortLinkList()));
			if (CollectionUtils.isEmpty(linkDOList)) {
				return response;
			}

			// 批量更新为失效状态
			linkDAO.updateStatusByIdList(LinkStatusEnum.NOT_AVAILABLE.getStatus(), linkDOList.stream().map(LinkDO::getId).collect(Collectors.toList()));

			// 删缓存
			asyncBatchRemoveShortLinkCache(linkDOList);
		} catch (Exception e) {
			log.error("deleteShortLink error, request={}", JSONObject.toJSONString(request), e);
			response.setSuccess(false);
			return response;
		}

		return response;
	}

	@Override
	public void asyncBatchRemoveShortLinkCache(List<LinkDO> linkDOList) {
		// 异步删缓存
		CompletableFuture.runAsync(() -> {
			// 删缓存
			batchRemoveShortLinkCache(linkDOList);
		}, bizThreadPool).whenComplete((r, t) -> {
			if (t != null) {
				log.error("deleteShortLink asyncBatchRemoveShortLinkCache error", t);
			}

			// 延时再删
			scheduledThreadPoolExecutor.schedule(() -> batchRemoveShortLinkCache(linkDOList), 1, TimeUnit.SECONDS);
		});
	}

	public void batchRemoveShortLinkCache(List<LinkDO> linkDOList) {
		Set<String> shortLinkSet = new HashSet<>(linkDOList.size());
		Set<String> longLinkSet = new HashSet<>(linkDOList.size());
		for (LinkDO linkDO : linkDOList) {
			shortLinkSet.add(LinkLambda.SHORT_LINK_KEY_APPEND.apply(linkDO.getShortSuffix()));
			longLinkSet.add(LinkLambda.LONG_LINK_KEY_APPEND.apply(linkDO.getLongLink()));
		}

		// 删除本地缓存短链
		shortSuffixCache.invalidateAll(shortLinkSet);
		// 删除本地缓存长链
		longLinkCache.invalidateAll(longLinkSet);

		// 删除redis缓存
		RKeys keys = redissonClient.getKeys();
		keys.unlink(shortLinkSet.toArray(new String[0]));
		keys.unlink(longLinkSet.toArray(new String[0]));
	}

	@Override
	public UpdateLinkResponseVO updateLink(UpdateLinkRequestVO request) {
		LinkAssert.notEmpty(request, "request is empty");
		LinkAssert.isTrue(CollectionUtils.isNotEmpty(request.getUpdateLinkList()), "updateLinkList is empty");

		UpdateLinkResponseVO response = new UpdateLinkResponseVO();

		// 待更新link集合
		List<LinkVO> updateLinkList = request.getUpdateLinkList().stream().filter(link -> StringUtils.isNotBlank(link.getShortSuffix())).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(updateLinkList)) {
			response.setSuccess(false);
			return response;
		}

		for (LinkVO linkVO : updateLinkList) {
			try {
				LinkDO linkDO = ConvertUtil.linkVoConvertToLinDo(linkVO);
				// 更新数据库
				linkDAO.updateByShortSuffix(linkDO);

				// 删缓存
				asyncBatchRemoveShortLinkCache(ListUtil.newArrayList(linkDO));
			} catch (Exception e) {
				log.error("updateLink error, linkVO={}", JSONObject.toJSONString(linkVO));
			}
		}

		return response;
	}

}
