package com.magicrepokit.chat.event.listener;

import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.chat.component.LangchainComponent;
import com.magicrepokit.chat.constant.KnowledgeConstant;
import com.magicrepokit.chat.entity.KnowledgeDetail;
import com.magicrepokit.chat.event.KnowledgeProcessEvent;
import com.magicrepokit.chat.service.IKnowledgeDetailService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final LangchainComponent langchainComponent;
    @Override
    @Async
    public void onApplicationEvent(KnowledgeProcessEvent event) {
        Integer type = event.getType();
        if(type.equals(1)){
            processSingle(event);
        }else if(type.equals(2)){
            processBatch(event);
        }
    }

    /**
     * 批量处理
     * @param event
     */
    private void processBatch(KnowledgeProcessEvent event) {
        List<KnowledgeDetail> knowledgeDetailList = event.getKnowledgeDetailList();

        for (KnowledgeDetail knowledgeDetail : knowledgeDetailList) {
            processSingle(new KnowledgeProcessEvent(event.getIndexName(),knowledgeDetail));
        }
    }

    /**
     * 单文件处理
     * @param event 事件
     */
    private void processSingle(KnowledgeProcessEvent event) {
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
                    .embeddingModel(langchainComponent.getDefaultEmbeddingModel())
                    .embeddingStore(langchainComponent.getDefaultElasticsearchEmbeddingStore(event.getIndexName()))
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
    /**
     * 改变状态
     * @param knowledgeDetail  知识库详情
     * @param statusType 状态类型
     * @param error 错误信息
     */
    private void changeStatus(KnowledgeDetail knowledgeDetail, Integer statusType,String error) {
        knowledgeDetail.setStatus(statusType);
        knowledgeDetail.setErrorMsg("");
        if(ObjectUtil.isNotNull(error)){
            knowledgeDetail.setErrorMsg(error);
        }
        knowledgeDetailService.updateById(knowledgeDetail);
    }

    /**
     * 获取文档解析器
     * @param fileType 文件类型
     * @return DocumentParser
     */
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
