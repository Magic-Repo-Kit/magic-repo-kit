package com.gpt.langchain.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gpt.langchain")
@Getter
@Setter
public class ConfigProperties {
    /**
     * 是否启用
     */
    private Boolean enabled=false;
    /**
     * elasticSearch的地址
     */
    private String elasticHost;
    /**
     * elasticSearch的端口
     */
    private Integer elasticPort;

    /**
     * elasticSearch的用户名
     */
    private String elasticUsername;

    /**
     * elasticSearch的密码
     */
    private String elasticPassword;
}
