package com.lcm.passport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.lcm"})
public class QiushibaikePassportApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiushibaikePassportApplication.class, args);
    }

}
