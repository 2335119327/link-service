package com.link.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author : wangaidong
 * @date : 2023/11/28 14:41
 */
@Data
@TableName("link")
public class LinkDO {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 状态（1: 可用，0: 不可用）
	 */
	private Integer status;

	/**
	 * 短链后缀
	 */
	private String shortSuffix;

	/**
	 * 长链
	 */
	private String longLink;

	/**
	 * 过期时间
	 */
	private LocalDateTime expireTime;

	/**
	 * 添加时间
	 */
	private LocalDateTime addTime;

	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;

}
