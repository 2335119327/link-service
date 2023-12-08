package com.link.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.link.model.entity.LinkDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : wangaidong
 * @date : 2023/11/28 14:44
 */
@Mapper
public interface LinkDAO extends BaseMapper<LinkDO> {

	/**
	 * 更新状态
	 */
	void updateStatusByIdList(@Param("status") Integer status, @Param("idList") List<Long> idList);

	/**
	 * 分页查询过期的link
	 */
	List<LinkDO> pageExpireLink(@Param("maxId") Long maxId,
								@Param("limitVal") Integer limitVal,
	                            @Param("shardIndex") int shardIndex,
	                            @Param("shardTotal") int shardTotal);

	/**
	 * updateByShortSuffix
	 */
	void updateByShortSuffix(LinkDO linkDO);

}
