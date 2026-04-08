package com.codecart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.codecart.mapper")
@SpringBootApplication
public class CodeCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeCartApplication.class, args);
    }
}
