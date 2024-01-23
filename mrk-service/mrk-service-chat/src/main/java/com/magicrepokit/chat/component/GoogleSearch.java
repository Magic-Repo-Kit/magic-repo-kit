package com.magicrepokit.chat.component;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class GoogleSearch {
    private String apiKey="";
    private String cx="";


    private RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        // 设置代理
        requestFactory.setProxy(createProxy("127.0.0.1", 7890));
        RestTemplate restTemplate = new RestTemplate();
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



    public String searchGoogle(String query) {
        RestTemplate restTemplate = getRestTemplate();
        String url = UriComponentsBuilder
                .fromHttpUrl("https://www.googleapis.com/customsearch/v1")
                .queryParam("key", apiKey)
                .queryParam("cx", cx)
                .queryParam("q", query)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}
