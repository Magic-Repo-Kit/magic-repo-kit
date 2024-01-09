package com.magicrepokit.chat.event.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.magicrepokit.chat.constant.KnowledgeConstant;
import com.magicrepokit.chat.entity.KnowledgeDetail;
import com.magicrepokit.chat.event.KnowledgeProcessEvent;
import com.magicrepokit.chat.service.IKnowledgeDetailService;
import com.magicrepokit.langchain.config.ConfigProperties;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

@Slf4j
@Component
@AllArgsConstructor
public class KnowledgeProcessListener implements ApplicationListener<KnowledgeProcessEvent> {
    private final IKnowledgeDetailService knowledgeDetailService;
    private ConfigProperties langchainConfigProperties;
    @Override
    @Async
    public void onApplicationEvent(KnowledgeProcessEvent event) {
        long start = System.currentTimeMillis();
        KnowledgeDetail knowledgeDetail = event.getKnowledgeDetail();
        try {
            log.info("{}:文件开始处理",knowledgeDetail.getName());
            //开始文件分隔
            Document document = UrlDocumentLoader.load("http://"+knowledgeDetail.getFileUrl(), getDocumentParser(knowledgeDetail.getType()));
            log.info("{}:文件开始处理-分隔",knowledgeDetail.getName());
            DocumentSplitter documentSplitter = DocumentSplitters.recursive(500, 100, new OpenAiTokenizer(GPT_3_5_TURBO));
            //2.更新状态
            changeStatus(knowledgeDetail,KnowledgeConstant.FILE_SPLITTING,null);
            //开始训练
            log.info("{}:文件开始处理-训练",knowledgeDetail.getName());
            changeStatus(knowledgeDetail,KnowledgeConstant.TRAINING,null);
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(documentSplitter)
                    .embeddingModel(getEmbeddingModel())
                    .embeddingStore(getElasticsearchEmbeddingStore(event.getIndexName()))
                    .build();
            ingestor.ingest(document);
            changeStatus(knowledgeDetail,KnowledgeConstant.COMPLETE,null);
        }catch (Exception e){
            log.error("{}:文件开始处理-失败:{}",knowledgeDetail.getName(),e.getMessage());
            changeStatus(knowledgeDetail,KnowledgeConstant.FAIL,e.getMessage());
            return;
        }
        log.info("{}:文件开始处理-完成,耗时{}ms",knowledgeDetail.getName(),System.currentTimeMillis()-start);
    }


    private EmbeddingModel getEmbeddingModel(){
        return OpenAiEmbeddingModel.builder().apiKey("sk-gRbZ9FJz2E7c7mwO5JOvp2u2rtoWoAbg12CxDy3Y25eLeDvd").baseUrl("https://api.chatanywhere.tech/v1").build();
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

    private void changeStatus(KnowledgeDetail knowledgeDetail, Integer statusType,String error) {
        knowledgeDetail.setStatus(statusType);
        knowledgeDetail.setErrorMsg("");
        if(ObjectUtil.isNotNull(error)){
            knowledgeDetail.setErrorMsg(error);
        }
        knowledgeDetailService.updateById(knowledgeDetail);
    }

    private DocumentParser getDocumentParser(String fileType){
        if(fileType.equals("pdf")){
            return new ApachePdfBoxDocumentParser();
        }
        if(fileType.equals("doc")){
            return new ApachePoiDocumentParser();
        }
        //txt
        return new TextDocumentParser();
    }
}
