package com.magicrepokit.chat;

import com.magicrepokit.chat.agent.CustomerSupportAgent;
import com.magicrepokit.chat.service.tool.BookingTools;
import com.magicrepokit.oss.OssTemplate;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;

import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;


import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;


@Configuration
public class TestConfiguration {

    /**
     * 创建openai chat model
     * @return
     */
    @Bean
    ChatLanguageModel chatLanguageModel(){
        return OpenAiChatModel.builder().apiKey("sk-gRbZ9FJz2E7c7mwO5JOvp2u2rtoWoAbg12CxDy3Y25eLeDvd").baseUrl("https://api.chatanywhere.tech/").build();
    }


    /**
     * 创建客服代理
     */

    @Bean
    CustomerSupportAgent customerSupportAgent(ChatLanguageModel chatLanguageModel, BookingTools bookingTools, Retriever<TextSegment> retriever) {
        return AiServices.builder(CustomerSupportAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .tools(bookingTools)
                .retriever(retriever)
                .build();
    }

    /**
     * 创建retriever
     * @param embeddingStore
     * @param embeddingModel
     * @return
     */
    @Bean
    Retriever<TextSegment> retriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {

        // You will need to adjust these parameters to find the optimal setting, which will depend on two main factors:
        // - The nature of your data
        // - The embedding model you are using
        int maxResultsRetrieved = 1;
        double minScore = 0.6;

        return EmbeddingStoreRetriever.from(embeddingStore, embeddingModel, maxResultsRetrieved, minScore);
    }

    /**
     * 创建embedding model
     * @return
     */
    @Bean
    EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder().apiKey("sk-gRbZ9FJz2E7c7mwO5JOvp2u2rtoWoAbg12CxDy3Y25eLeDvd").baseUrl("https://api.chatanywhere.tech/v1").build();
    }

    /**
     * 创建embedding store
     * @param embeddingModel
     * @param resourceLoader
     * @return
     * @throws IOException
     */
    @Bean
    EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel, ResourceLoader resourceLoader) throws IOException {

        // Normally, you would already have your embedding store filled with your data.
        // However, for the purpose of this demonstration, we will:

        // 1. Create an in-memory embedding store
        //EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        ElasticsearchEmbeddingStore embeddingStore = ElasticsearchEmbeddingStore.builder()
                .serverUrl("154.204.60.125:9200")
                .userName("elastic")
                .password("123456")
                .indexName("mrk_gpt_knowledge2")
                .dimension(1536)
                .build();

        // 2. Load an example document ("Miles of Smiles" terms of use)
        Resource resource = resourceLoader.getResource("classpath:miles-of-smiles-terms-of-use.txt");
        Document document = loadDocument(resource.getFile().toPath(), new TextDocumentParser());

        // 3. Split the document into segments 100 tokens each
        // 4. Convert segments into embeddings
        // 5. Store embeddings into embedding store
        // All this can be done manually, but we will use EmbeddingStoreIngestor to automate this:
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(100, 0, new OpenAiTokenizer(GPT_3_5_TURBO));
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);

        return embeddingStore;
    }


}
