package com.ssafy.happymeal.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.ssafy.happymeal.domain")
public class MybatisConfig {

}
