package com.ssafy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 활성화
//@MapperScan("com.ssafy.happymeal.domain.user.dao")
public class HappyMealApplication {

	public static void main(String[] args) {
		SpringApplication.run(HappyMealApplication.class, args);
	}

}
