package com.neworange;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/15 16:51
 * @description
 */
@EnableAsync
@SpringBootApplication(exclude ={DataSourceAutoConfiguration.class})
@MapperScan("com.neworange.dao")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
