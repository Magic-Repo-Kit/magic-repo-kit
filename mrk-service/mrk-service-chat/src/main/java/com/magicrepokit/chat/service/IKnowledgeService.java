package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.dto.KnowledgePageDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.vo.KnowledgePageVO;
import com.magicrepokit.chat.vo.KnowledgeVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.mb.base.BaseService;

public interface IKnowledgeService extends BaseService<Knowledge> {

    /**
     * 创建文件夹或文件
     * @param createDTO
     * @return
     */
    KnowledgeVO create(KnowledgeCreateDTO createDTO);

    /**
     * 分页
     * @param knowledgePageDTO
     * @return
     */
    PageResult<KnowledgePageVO> page(KnowledgePageDTO knowledgePageDTO);
}
