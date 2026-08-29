package com.syzua.copyright;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.syzua.copyright.mapper")
public class CopyrightApplication {

    public static void main(String[] args) {
        SpringApplication.run(CopyrightApplication.class, args);
    }
}
