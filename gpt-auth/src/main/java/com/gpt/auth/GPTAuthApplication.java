package com.gpt.auth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.gpt")
@EnableFeignClients({"com.gpt"})
public class GPTAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(GPTAuthApplication.class);
    }
}
