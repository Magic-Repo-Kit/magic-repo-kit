package com.magicrepokit.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.magicrepokit")
@MapperScan("com.magicrepokit.**.mapper")
public class MRKUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MRKUserApplication.class);
    }
}
