package com.gpt.chat.event;

import com.gpt.chat.entity.KnowledgeDetail;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;


@ToString
@Getter
public class KnowledgeProcessEvent extends ApplicationEvent {
    private KnowledgeDetail knowledgeDetail;
    private List<KnowledgeDetail> knowledgeDetailList;
    private final String indexName;
    //1:单处理 2:批量处理
    private final Integer type;
    public KnowledgeProcessEvent(String indexName,KnowledgeDetail knowledgeDetail) {
        super(knowledgeDetail);
        this.knowledgeDetail = knowledgeDetail;
        this.indexName = indexName;
        this.type = 1;
    }

    public KnowledgeProcessEvent(String indexName,List<KnowledgeDetail> knowledgeDetailList) {
        super(knowledgeDetailList);
        this.knowledgeDetailList = knowledgeDetailList;
        this.indexName = indexName;
        this.type = 2;
    }
}
