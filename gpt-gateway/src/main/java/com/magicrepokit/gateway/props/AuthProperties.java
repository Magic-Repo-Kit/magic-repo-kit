package com.magicrepokit.gateway.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@RefreshScope
@Component
@ConfigurationProperties("mrk.secure")
public class AuthProperties {
    /**
     * 放行API集合
     */
    private final List<String> skipUrl = new ArrayList<>();
}
