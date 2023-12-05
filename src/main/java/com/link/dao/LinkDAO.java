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

}
