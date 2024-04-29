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
     * 获取分词器
     *
     * @param gptModel
     * @return
     */
    public Tokenizer getOpenAiTokenizer(GptModel gptModel) {
        return new OpenAiTokenizer(gptModel.getAcutualModelName());
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






}
