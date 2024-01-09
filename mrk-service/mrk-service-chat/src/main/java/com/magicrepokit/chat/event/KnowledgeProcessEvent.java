package com.magicrepokit.chat.event;

import com.magicrepokit.chat.entity.KnowledgeDetail;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;


@ToString
public class KnowledgeProcessEvent extends ApplicationEvent {
    private KnowledgeDetail knowledgeDetail;
    private String indexName;
    public KnowledgeProcessEvent(String indexName,KnowledgeDetail knowledgeDetail) {
        super(knowledgeDetail);
        this.knowledgeDetail = knowledgeDetail;
        this.indexName = indexName;

    }

    public KnowledgeDetail getKnowledgeDetail() {
        return knowledgeDetail;
    }

    public String getIndexName() {
        return indexName;
    }
}
