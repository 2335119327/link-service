package com.link.utils;

import lombok.Data;

/**
 * @author : wangaidong
 * @date : 2023/11/29 18:36
 * @description : Pair
 */
@Data
public class Pair<Left, Right> {

	private Left left;

	private Right right;

	public Pair(Left left, Right right) {
		this.left = left;
		this.right = right;
	}

	public static <Left, Right> Pair<Left, Right> of(Left left, Right right) {
		return new Pair<>(left, right);
	}

}
