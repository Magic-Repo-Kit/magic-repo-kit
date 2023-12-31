package com.magicrepokit.langchain.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static dev.langchain4j.internal.Utils.isNullOrBlank;

@Configuration
public class LangChainConfiguration {
    /**
     * 加载elasticSearch客户端
     */
    @Bean
    public ElasticsearchClient elasticDataOperation(ConfigProperties configProperties) {
        String elasticUrl = configProperties.getElasticHost();
        Integer elasticPort = configProperties.getElasticPort();
        if(ObjectUtil.isEmpty(configProperties.getElasticHost())||ObjectUtil.isEmpty(configProperties.getElasticPort())){
            return null;
        }
        String url = StrUtil.format("{}:{}", elasticUrl, elasticPort);
        RestClientBuilder restClientBuilder = RestClient
                .builder(HttpHost.create(url));

        if (!isNullOrBlank(configProperties.getElasticUsername())) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(configProperties.getElasticUsername(), configProperties.getElasticPassword()));
            restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(provider));
        }

        ElasticsearchTransport transport = new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }
}
