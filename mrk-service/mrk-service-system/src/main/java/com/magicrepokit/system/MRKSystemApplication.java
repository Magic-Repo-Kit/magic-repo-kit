package com.magicrepokit.system;

import com.fasterxml.classmate.TypeResolver;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;


@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.magicrepokit")
@MapperScan("com.magicrepokit.**.mapper")
@EnableFeignClients({"com.magicrepokit"})
@EnableCaching
@EnableSwagger2
public class MRKSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MRKSystemApplication.class);
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.magicrepokit"))
                .paths(PathSelectors.any()).build().pathMapping("/").directModelSubstitute(LocalDate.class, String.class);

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("技术学习实践整合")
                .description("新技术整合学习及配置案例DEMO")
                .contact(new Contact("治功", "https://juejin.cn/user/2806404106423112", "邮箱"))
                .version("V1.0")
                .build();
    }
}
