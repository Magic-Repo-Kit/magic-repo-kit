package com.magicrepokit.chat.component;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.C;
import com.magicrepokit.langchain.config.ConfigProperties;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class LangchainComponent {
    private ConfigProperties langchainConfigProperties;

    /**
     * 获取elasticsearch存储
     * @param indexName 索引名称
     * @return ElasticsearchEmbeddingStore
     */
    public ElasticsearchEmbeddingStore getElasticsearchEmbeddingStore(String indexName){
        if(!langchainConfigProperties.getEnabled()){
            log.error("未开启elasticsearch");
            return null;
        }
        String elasticHost = langchainConfigProperties.getElasticHost();
        int elasticPort = langchainConfigProperties.getElasticPort();
        String url = StrUtil.format("{}:{}", elasticHost, elasticPort);
        return ElasticsearchEmbeddingStore.builder()
                .serverUrl(url)
                .userName(langchainConfigProperties.getElasticUsername())
                .password(langchainConfigProperties.getElasticPassword())
                .indexName(indexName)
                .dimension(1536)
                .build();
    }


    /**
     * 向量检索
     * @param indexName 索引名称
     * @param question 问题
     * @return List<TextSegment>
     */
    public List<TextSegment> findRelevant(String indexName,String question){
        EmbeddingStoreRetriever embeddingStoreRetriever = new EmbeddingStoreRetriever(getElasticsearchEmbeddingStore(indexName),
                getEmbeddingModel(),
                5,
                0.7
        );
        return embeddingStoreRetriever.findRelevant(question);
    }

    /**
     * 向量检索
     * @param indexName 索引名称
     * @param question 问题
     * @param maxResult 最大结果
     * @param minScore 最小分数
     * @return List<TextSegment>
     */
    public List<TextSegment> findRelevant(String indexName,String question,int maxResult,double minScore){
        EmbeddingStoreRetriever embeddingStoreRetriever = new EmbeddingStoreRetriever(getElasticsearchEmbeddingStore(indexName),
                getEmbeddingModel(),
                maxResult,
                minScore
        );
        return embeddingStoreRetriever.findRelevant(question);
    }


    /**
     * 获取分词模型
     */
    public EmbeddingModel getEmbeddingModel(){
        return OpenAiEmbeddingModel.builder().apiKey("sk-gRbZ9FJz2E7c7mwO5JOvp2u2rtoWoAbg12CxDy3Y25eLeDvd").baseUrl("https://api.chatanywhere.tech/v1").build();
    }

}
