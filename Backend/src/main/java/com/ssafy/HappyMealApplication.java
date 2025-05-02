package com.ssafy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.ssafy.happymeal.domain.user.dao")
public class HappyMealApplication {

	public static void main(String[] args) {
		SpringApplication.run(HappyMealApplication.class, args);
	}

}
