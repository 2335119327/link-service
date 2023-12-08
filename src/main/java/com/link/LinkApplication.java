package com.link;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : wangaidong
 * @date : 2023/11/28 14:35
 */
@MapperScan("com.link.dao")
@SpringBootApplication
public class LinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkApplication.class, args);
	}

}
