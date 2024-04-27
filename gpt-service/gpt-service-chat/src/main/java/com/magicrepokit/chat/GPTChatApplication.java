package com.magicrepokit.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.magicrepokit")
@MapperScan("com.magicrepokit.**.mapper")
@EnableCaching
@EnableAsync
public class GPTChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(GPTChatApplication.class);
    }
}
