package com.magicrepokit.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
public class RestTemplateConfig {
    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        // Set the proxy details
//        requestFactory.setProxy(createProxy("127.0.0.1", 7890));
        RestTemplate restTemplate = new RestTemplate();
        //配置
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401&& response.getRawStatusCode() != 403) {
                    super.handleError(response);
                }
            }
        });
        return restTemplate;
    }

    private static Proxy createProxy(String proxyHost, int proxyPort) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
    }
}
