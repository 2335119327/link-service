package com.link.utils.lambda;

/**
 * @author : wangaidong
 * @date : 2023/11/30 19:22
 */
public interface ThreeFunction<T1, T2, T3, R> {

	R apply(T1 t1, T2 t2, T3 t3);

}
