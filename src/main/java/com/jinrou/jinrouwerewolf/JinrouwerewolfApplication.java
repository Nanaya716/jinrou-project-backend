package com.jinrou.jinrouwerewolf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.jinrou.jinrouwerewolf.dao")
@EnableCaching
@EnableScheduling
public class JinrouwerewolfApplication {

    public static void main(String[] args) {
        SpringApplication.run(JinrouwerewolfApplication.class, args);
    }

}
