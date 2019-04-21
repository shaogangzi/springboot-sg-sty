package com.sg.springbootsgsty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan(basePackages = "com.sg.springbootsgsty.dao")
public class SpringbootSgStyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootSgStyApplication.class, args);
	}

}
