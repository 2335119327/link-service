package com.link.utils.lambda;

/**
 * @author : wangaidong
 * @date : 2023/11/29 19:31
 */
public interface ThreeConsumer<K1, K2, K3> {

	/**
	 * apply
	 */
	void accept(K1 k1, K2 k2, K3 k3);

}
