package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.dto.KnowledgeListDTO;
import com.magicrepokit.chat.dto.KnowledgeMoveDTO;
import com.magicrepokit.chat.dto.KnowledgeProcessDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.vo.KnowledgeFileVO;
import com.magicrepokit.chat.vo.KnowledgeListVO;
import com.magicrepokit.chat.vo.KnowledgePathVO;
import com.magicrepokit.chat.vo.KnowledgeVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.mb.base.BaseService;

import java.util.List;

public interface IKnowledgeService extends BaseService<Knowledge> {

    /**
     * 创建文件夹或文件
     * @param createDTO
     * @return
     */
    KnowledgeVO create(KnowledgeCreateDTO createDTO);

    /**
     * 分页
     * @param knowledgeListDTO
     * @return
     */
    List<KnowledgeListVO> list(KnowledgeListDTO knowledgeListDTO);

    /**
     * 根据父节点id查询当前路径
     * @param parentId
     * @return
     */
    List<KnowledgePathVO> listPathByParentId(Long parentId);

    /**
     * 移动文件或文件夹
     * @param knowledgeMoveDTO
     * @return
     */
    boolean move(KnowledgeMoveDTO knowledgeMoveDTO);

    /**
     * 删除文件或文件夹
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 文件处理
     * @param knowledgeProcessDTO
     * @return
     */
    boolean process(KnowledgeProcessDTO knowledgeProcessDTO);

    /**
     * 获取文件详情
     * @param id
     * @return
     */
    KnowledgeFileVO detailFile(Long id);

    /**
     * 文件处理状态列表
     * @return
     */
    List<KnowledgeFileVO> listFileStatus();

    /**
     * 文件列表
     * @param knowledgeId
     * @return
     */
    List<KnowledgeFileVO> listFile(Long knowledgeId);


    /**
     * 重新处理
     * @param knowledgeDetailId
     * @return
     */
    boolean reprocess(Long knowledgeDetailId);
}
