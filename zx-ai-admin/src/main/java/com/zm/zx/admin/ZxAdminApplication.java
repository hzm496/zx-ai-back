package com.zm.zx.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan({"com.zm.zx.admin.mapper","com.zm.zx.common.mapper"})
@ComponentScan(basePackages = {"com.zm.zx"})
@SpringBootApplication
public class ZxAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZxAdminApplication.class, args);
    }
}
