package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.knowledge.*;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.vo.knowledge.*;
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
    PageResult<KnowledgeListVO> page(KnowledgeListDTO knowledgeListDTO);

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
     * @param knowledgeIds
     * @return
     */
    boolean delete(String knowledgeIds);

    /**
     * 文件处理
     * @param knowledgeProcessDTO
     * @return
     */
    boolean process(KnowledgeProcessDTO knowledgeProcessDTO);

    /**
     * 文件内容上传处理(多文件处理)
     * @param knowledgeProcessBatchDTO
     * @return
     */
    boolean processBatch(KnowledgeProcessBatchDTO knowledgeProcessBatchDTO);

    /**
     * 重新处理
     * @param knowledgeDetailId
     * @return
     */
    boolean reprocess(Long knowledgeDetailId);

    /**
     * 获取文件详情
     * @param id
     * @return
     */
    KnowledgeFileListVO detailFile(Long id);

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
     * 删除文件内容
     * @param knowledgeIds
     * @return
     */
    boolean deleteFile(String knowledgeIds);

    /**
     * 文件内容详情
     * @param knowledgeId
     * @return
     */
    boolean checkIsFile(Long knowledgeId);

    /**
     * 修改文件夹或文件
     * @param updateDTO
     * @return
     */
    KnowledgeVO updateByDto(KnowledgeUpdateDTO updateDTO);
}
