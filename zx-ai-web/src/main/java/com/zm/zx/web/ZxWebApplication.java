package com.zm.zx.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@MapperScan({"com.zm.zx.web.mapper","com.zm.zx.common.mapper"})
@ComponentScan(basePackages = {"com.zm.zx"})
@SpringBootApplication
public class ZxWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZxWebApplication.class, args);
    }
}
