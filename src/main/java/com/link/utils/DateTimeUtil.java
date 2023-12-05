package com.link.utils;

import com.link.constants.constant.LinkConstant;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author : wangaidong
 * @date : 2023/11/29 13:00
 * @description : 时间工具嘞
 */
public class DateTimeUtil {

	public static LocalDateTime epochMilliToBeijingTime(long epochMilli) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), LinkConstant.BEIJING_ZONE);
	}

}
