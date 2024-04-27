package com.gpt.chat.service;

import com.gpt.chat.entity.KnowledgeDetail;
import com.gpt.mb.base.BaseService;

import java.util.List;

public interface IKnowledgeDetailService extends BaseService<KnowledgeDetail> {

    /**
     * 查询未完成的文件
     * @return
     */
    List<KnowledgeDetail> listNotCompleted();

    /**
     * 根据知识id查询文件
     * @param knowledgeId
     * @return
     */
    List<KnowledgeDetail> listByKnowledgeId(Long knowledgeId);

    /**
     * 根据知识id,文件名查询文件是否存在
     * @param knowledgeId
     * @return
     */
    boolean checkIsFileExist(Long knowledgeId, String fileName);

    /**
     * 根据知识id存在文件
     * @param knowledgeId
     * @return
     */
    boolean checkHasFile(Long knowledgeId);

    /**
     * 根据知识id,文件名批量查询文件是否存在
     * @param id
     * @param fileNames
     * @return
     */
    boolean checkIsFileBatchExist(Long id, List<String> fileNames);

    /**
     * 根据知识id批量删除文件
     * @param knowledgeIdList
     * @return
     */
    boolean deleteByKnowledgeIds(List<Long> knowledgeIdList);
}
