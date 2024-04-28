package com.gpt.chat.component;

import cn.hutool.core.util.StrUtil;
import com.gpt.chat.constant.GptModel;
import com.gpt.langchain.config.ConfigProperties;
import com.gpt.log.exceotion.ServiceException;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
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

    public StreamingChatLanguageModel getLlamaChatModel(GptModel gptModel){
        String acutualModelName = gptModel.getAcutualModelName();
        if(!acutualModelName.startsWith("llama")){
            throw new ServiceException("只支持llama模型");
        }
        return OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434/")
                .modelName(acutualModelName)
                .temperature(0.7)
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
