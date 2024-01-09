package com.magicrepokit.chat.service;

import com.magicrepokit.chat.entity.KnowledgeDetail;
import com.magicrepokit.mb.base.BaseService;

import java.util.List;

public interface IKnowledgeDetailService extends BaseService<KnowledgeDetail> {

    /**
     * 查询未完成的文件
     * @return
     */
    List<KnowledgeDetail> listNotCompleted();

    List<KnowledgeDetail> listByKnowledgeId(Long knowledgeId);

    boolean checkIsFileExist(Long knowledgeId, String fileName);
}
