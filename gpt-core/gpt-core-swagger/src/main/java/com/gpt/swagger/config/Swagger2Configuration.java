package com.gpt.swagger.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;

@EnableSwagger2
@AutoConfiguration
public class Swagger2Configuration {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("xxxxx"))
                .paths(PathSelectors.any()).build().pathMapping("/").directModelSubstitute(LocalDate.class, String.class);

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("GPT管理系统接口文档")
                .description("GPT管理系统标注化接口文档")
                .contact(new Contact("AuroralPixel", "xxxxx", "xxxxx"))
                .version("V1.0")
                .build();
    }
}
