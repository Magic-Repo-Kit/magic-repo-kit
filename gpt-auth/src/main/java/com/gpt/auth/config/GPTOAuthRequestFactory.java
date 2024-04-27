package com.gpt.auth.config;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GPTOAuthRequestFactory extends DefaultOAuth2RequestFactory {
    public GPTOAuthRequestFactory(ClientDetailsService clientDetailsService) {
        super(clientDetailsService);
    }

    @Override
    public TokenRequest createTokenRequest(Map<String, String> requestParameters, ClientDetails authenticatedClient) {
        // 解码参数值
        Map<String, String> decodedParameters = new HashMap<>();
        for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 解码参数值
            try {
                value = URLDecoder.decode(value, String.valueOf(StandardCharsets.UTF_8));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            decodedParameters.put(key, value);
        }

        return super.createTokenRequest(decodedParameters, authenticatedClient);
    }
}
