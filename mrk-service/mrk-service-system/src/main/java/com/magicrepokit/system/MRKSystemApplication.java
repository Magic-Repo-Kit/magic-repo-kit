package com.magicrepokit.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.magicrepokit")
@MapperScan("com.magicrepokit.**.mapper")
@EnableFeignClients({"com.magicrepokit"})
@EnableCaching
public class MRKSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MRKSystemApplication.class);
    }
}
