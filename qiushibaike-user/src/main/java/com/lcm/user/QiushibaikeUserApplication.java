package com.lcm.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.lcm"})
public class QiushibaikeUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiushibaikeUserApplication.class, args);
    }

}
