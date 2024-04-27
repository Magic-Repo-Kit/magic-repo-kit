package com.gpt.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import static springfox.documentation.schema.AlternateTypeRules.newRule;


@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.gpt")
@MapperScan("com.gpt.system.mapper")
@EnableFeignClients({"com.gpt"})
@EnableCaching
public class GPTSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(GPTSystemApplication.class);
    }
}
