package com.gpt.chat.component;

import cn.hutool.core.util.StrUtil;
import com.gpt.chat.constant.GptModel;
import com.gpt.langchain.config.ConfigProperties;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
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
     *
     * @param indexName 索引名称
     * @return ElasticsearchEmbeddingStore
     */
    public ElasticsearchEmbeddingStore getDefaultElasticsearchEmbeddingStore(String indexName) {
        if (!langchainConfigProperties.getEnabled()) {
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
     *
     * @param indexName 索引名称
     * @param question  问题
     * @return List<TextSegment>
     */
    public List<TextSegment> findRelevant(String indexName, String question) {
        EmbeddingStoreRetriever embeddingStoreRetriever = new EmbeddingStoreRetriever(getDefaultElasticsearchEmbeddingStore(indexName),
                getDefaultEmbeddingModel(),
                5,
                0.8
        );
        return embeddingStoreRetriever.findRelevant(question);
    }

    /**
     * 向量检索
     *
     * @param indexName 索引名称
     * @param question  问题
     * @param maxResult 最大结果
     * @param minScore  最小分数
     * @return List<TextSegment>
     */
    public List<TextSegment> findRelevant(String indexName, String question, int maxResult, double minScore) {
        if(maxResult<=0){
            maxResult=5;
        }
        if(minScore<=0){
            minScore=0.7;
        }
        EmbeddingStoreRetriever embeddingStoreRetriever = new EmbeddingStoreRetriever(getDefaultElasticsearchEmbeddingStore(indexName),
                getDefaultEmbeddingModel(),
                maxResult,
                minScore
        );
        return embeddingStoreRetriever.findRelevant(question);
    }


    /**
     * 获取分词模型
     */
    public EmbeddingModel getDefaultEmbeddingModel() {
        return OpenAiEmbeddingModel.builder().apiKey("sk-gRbZ9FJz2E7c7mwO5JOvp2u2rtoWoAbg12CxDy3Y25eLeDvd").baseUrl("https://api.chatanywhere.tech/v1").build();
    }

    /**
     * 获取分词器
     *
     * @param gptModel
     * @return
     */
    public Tokenizer getOpenAiTokenizer(GptModel gptModel) {
        return new OpenAiTokenizer(gptModel.getAcutualModelName());
    }

    /**
     * 获取默认聊天模型
     *
     * @return StreamingChatLanguageModel
     */
    private StreamingChatLanguageModel getStreamingDefaultChatLanguageModel(GptModel gptModel) {
        return OpenAiStreamingChatModel.builder()
                .apiKey("sk-gRbZ9FJz2E7c7mwO5JOvp2u2rtoWoAbg12CxDy3Y25eLeDvd")
                .baseUrl("https://api.chatanywhere.tech/")
                .modelName(gptModel.getAcutualModelName())
                .build();
    }

    private StreamingChatLanguageModel getStreamingDefaultChatLanguageModel(GptModel gptModel,Double temperature) {
        return OpenAiStreamingChatModel.builder()
                .apiKey("sk-gRbZ9FJz2E7c7mwO5JOvp2u2rtoWoAbg12CxDy3Y25eLeDvd")
                .baseUrl("https://api.chatanywhere.tech/")
                .modelName(gptModel.getAcutualModelName())
                .temperature(temperature)
                .build();
    }

    /**
     * 获取聊天模型
     *
     * @return StreamingChatLanguageModel
     */
    public StreamingChatLanguageModel getStreamingChatLanguageModel(GptModel gptModel) {
        //TODO 获取用户信息 1.查询用户key 2.如果有使用用户，如果没有使用默认
        return getStreamingDefaultChatLanguageModel(gptModel);
    }

    public StreamingChatLanguageModel getStreamingChatLanguageModel(GptModel gptModel,Double temperature) {
        //TODO 获取用户信息 1.查询用户key 2.如果有使用用户，如果没有使用默认
        return getStreamingDefaultChatLanguageModel(gptModel,temperature);
    }

    public SystemMessage getDefalutSystemMessage(GptModel gptModel){
        String modelName = gptModel.getModelName();
        String prompt = "你是gpt研发的"+modelName+"模型。别人问你有关你的身份信息，你可以回答：我是gpt研发的"+modelName+"模型。";
        return new SystemMessage(prompt);
    }






}
