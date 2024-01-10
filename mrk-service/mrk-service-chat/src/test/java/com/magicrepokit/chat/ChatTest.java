package com.magicrepokit.chat;



import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.magicrepokit.chat.agent.CustomerSupportAgent;
import com.magicrepokit.chat.service.tool.CalculatorService;
import com.magicrepokit.langchain.ElasticOperation;
import com.magicrepokit.langchain.config.ConfigProperties;
import com.magicrepokit.oss.OssTemplate;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class ChatTest {
    @Autowired
    private ChatLanguageModel model;

    @Autowired
    CustomerSupportAgent agent;

    @Autowired
    EmbeddingModel embeddingModel;

    @Autowired
    ElasticOperation elasticOperation;

    @Autowired
    OssTemplate ossTemplate;

    @Autowired
    ConfigProperties langchainConfigProperties;

    @Test
    public void testOssTemplate(){
        ossTemplate.makeBucket("test");
    }


    @Test
    public void simpleChat() {
        String response = model.generate("你好");
        System.out.println(response);
    }

    interface Assistant {
        @SystemMessage({"1.你是是一个数学专家，你只能回答数学方面的知识，如果用户内容与数学无关，你会只能回答：不知道!",
                "2.你可以使用工具类，使用之前你需要确定工具类的描述与用户问题相关，如果不相关，你会只能回答：不知道!"})
        String chat(String userMessage);
    }

    @Test
    public void chatWithTool() {
        Assistant build = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .tools(new CalculatorService())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10)).build();
        String question = "5/4=?";

        String answer = build.chat(question);

        System.out.println(answer);
    }

    @Test
    public void should_provide_booking_details_and_explain_why_cancellation_is_not_possible() {

        // Please define API keys in application.properties before running this test.
        // Tip: Use gpt-4 for this example, as gpt-3.5-turbo tends to hallucinate often and invent name and surname.

        interact(agent, "Hi, I forgot when my booking is.");
        interact(agent, "123-457");
        interact(agent, "I'm sorry I'm so inattentive today. Klaus Heisler.");
        interact(agent, "My bad, it's 123-456");
//
//        // Here, information about the cancellation policy is automatically retrieved and injected into the prompt.
//        // Although the LLM sometimes attempts to cancel the booking, it fails to do so and will explain
//        // the reason why the booking cannot be cancelled, based on the injected cancellation policy.
//        interact(agent, "My plans have changed, can I cancel my booking?");
    }

    private static void interact(CustomerSupportAgent agent, String userMessage) {
        System.out.println("==========================================================================================");
        System.out.println("[User]: " + userMessage);
        System.out.println("==========================================================================================");
        String agentAnswer = agent.chat(userMessage);
        System.out.println("==========================================================================================");
        System.out.println("[Agent]: " + agentAnswer);
        System.out.println("==========================================================================================");
    }

    private ElasticsearchEmbeddingStore getElasticsearchEmbeddingStore(String indexName){
        if(langchainConfigProperties.getEnabled()){
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
     * 创建EmbeddingStore with Elasticsearch
     */
    @Test
    public void createEmbeddingStoreWithElasticsearch() {

        //1.elsaticstore
        ElasticsearchEmbeddingStore embeddingStore = getElasticsearchEmbeddingStore("c2267fb9-7539-46b7-8aab-c1c8c532cbd5");

        Embedding content = embeddingModel.embed("这里石昊是谁").content();
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(content, 1, 0.9);
        System.out.println(relevant.get(0).score());
        System.out.println(relevant.get(0).embedded());
    }

    /**
     * 创建elastic客户端
     */
    @Test
    public void createElasticClient() {
        //1.判断索引是否存在
        System.out.println(elasticOperation.isIndexExist("mrk_gpt_knowledge2"));
        //2.根据id查询document
        System.out.println(elasticOperation.getDocumentById("mrk_gpt_knowledge2", "62c3470d-6f38-4b52-959e-988dc0721b01"));
        //3.删除索引
        System.out.println(elasticOperation.deleteIndex("mrk_gpt_knowledge2"));
    }

    @Test
    public void loadFromURL() {
        Document document = UrlDocumentLoader.load("http://s6ie5kuog.hd-bkt.clouddn.com/raipiot_user.txt", new TextDocumentParser());
        System.out.println(document.text());
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(500, 100, new OpenAiTokenizer(GPT_3_5_TURBO));
        List<TextSegment> split = documentSplitter.split(document);
        ObjectMapper objectMapper = ObjectMapper.of(split);
        JSONArray jsonArray = new JSONArray();
        objectMapper.map(jsonArray,null);
        System.out.println(jsonArray.toString());

    }

}
