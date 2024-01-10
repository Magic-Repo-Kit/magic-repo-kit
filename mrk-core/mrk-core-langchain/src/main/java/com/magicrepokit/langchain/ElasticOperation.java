package com.magicrepokit.langchain;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.magicrepokit.langchain.base.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ElasticOperation {
    @Autowired(required = false)
    private ElasticsearchClient elasticsearchClient;

    /**
     * 判断索引是否存在
     */
    public boolean isIndexExist(String indexName) {
        try {
            return elasticsearchClient.indices().exists(r -> r.index(indexName)).value();
        } catch (IOException e) {
            log.error("判断索引是否存在失败", e);
            return false;
        }
    }

    /**
     * 根据id查询document
     */
    public Document getDocumentById(String indexName, String id) {
        try {
            //判断索引是否存在
            if (!isIndexExist(indexName)) {
                return null;
            }
            //查询document
            GetResponse<Document> documentGetResponse = elasticsearchClient.get(r -> r.index(indexName).id(id), Document.class);
            boolean found = documentGetResponse.found();
            if (found) {
                return documentGetResponse.source();
            }
            return null;
        } catch (IOException e) {
            log.error("根据id查询document失败", e);
            return null;
        }
    }

    /**
     * 删除索引
     */
    public boolean deleteIndex(String indexName) {
        try {
            elasticsearchClient.indices().delete(r -> r.index(indexName));
            return true;
        } catch (IOException e) {
            log.error("删除索引失败", e);
            return false;
        }
    }

    /**
     * 批量删除索引
     */
    public boolean deleteIndex(List<String> indexNames) {
        try {
            elasticsearchClient.indices().delete(r -> r.index(indexNames));
            return true;
        } catch (IOException e) {
            log.error("批量删除索引失败", e);
            return false;
        }
    }
}
