package com.magicrepokit.chat.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.constant.KnowledgeConstant;
import com.magicrepokit.chat.converter.KnowledgeConverter;
import com.magicrepokit.chat.dto.knowledge.*;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.entity.KnowledgeDetail;
import com.magicrepokit.chat.event.KnowledgeProcessEvent;
import com.magicrepokit.chat.mapper.KnowledgeMapper;
import com.magicrepokit.chat.service.IKnowledgeDetailService;
import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.chat.vo.knowledge.*;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.utils.AuthUtil;
import com.magicrepokit.common.utils.StringUtil;
import com.magicrepokit.jwt.entity.MRKUser;
import com.magicrepokit.langchain.ElasticOperation;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mb.base.BaseServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class KnowledgeServiceImpl extends BaseServiceImpl<KnowledgeMapper, Knowledge> implements IKnowledgeService {
    private final KnowledgeConverter knowledgeConverter;
    private final IKnowledgeDetailService knowledgeDetailService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ElasticOperation elasticOperation;

    /**
     * 创建文件夹或文件
     * @param createDTO 创建文件夹或文件参数
     * @return 创建结果
     */
    @Override
    public KnowledgeVO create(KnowledgeCreateDTO createDTO) {
        Knowledge knowledge = knowledgeConverter.createDTOToEntity(createDTO);
        //1.查询父id是否存在
        Long parentId = createDTO.getParentId();
        if (ObjectUtil.isNotNull(parentId)) {
            Knowledge knowledgeById = getKnowledgeById(parentId);
            if (ObjectUtil.isNull(knowledgeById)) {
                throw new ServiceException(ChatResultCode.PARENT_ID_NOT_EXIST);
            }
            List<Long> pathId = knowledgeById.getPathId();
            if (ObjectUtil.isNull(pathId)) {
                pathId = new ArrayList<>();
            }
            pathId.add(parentId);
            knowledge.setPathId(pathId);
        }
        if(knowledge.getType().equals(KnowledgeConstant.FILE)){
            //1.索引为空生成索引
            if(ObjectUtil.isEmpty(knowledge.getIndexName())){
                knowledge.setIndexName(mkIndexName());
            }
            //2.匹配度
            knowledge.setMinScore(createDTO.getMinScore());
            //3.返回结果
            knowledge.setMaxResult(createDTO.getMaxResult());
        }else{
            knowledge.setIndexName(null);
            knowledge.setImageUrl(null);
        }
        this.save(knowledge);
        return knowledgeConverter.entityToVO(knowledge);
    }

    /**
     * 文件或文件夹列表
     * @param knowledgeListDTO 查询参数
     * @return 文件或文件夹列表
     */
    @Override
    public PageResult<KnowledgeListVO> page(KnowledgeListDTO knowledgeListDTO) {
        LambdaQueryWrapper<Knowledge> lambdaQueryWrapper = new LambdaQueryWrapper<Knowledge>();
        lambdaQueryWrapper.orderByDesc(Knowledge::getCreateTime);
        //1.id为空查询父一级
        if (ObjectUtil.isEmpty(knowledgeListDTO.getParentId())) {
            lambdaQueryWrapper.eq(Knowledge::getParentId, 0);
        } else {
            lambdaQueryWrapper.eq(Knowledge::getParentId, knowledgeListDTO.getParentId());
        }
        //2.模糊查询
        if (ObjectUtil.isNotEmpty(knowledgeListDTO.getKeywords())) {
            lambdaQueryWrapper.likeRight(Knowledge::getName, knowledgeListDTO.getKeywords());
        }
        //3.查询自己的 //TODO 后期根据权限控制
        MRKUser user = AuthUtil.getUser();
        if(ObjectUtil.isNull(user)){
            return null;
        }
        //4.类型
        if(ObjectUtil.isNotEmpty(knowledgeListDTO.getType())){
            lambdaQueryWrapper.eq(Knowledge::getType,knowledgeListDTO.getType());
        }
        lambdaQueryWrapper.eq(Knowledge::getCreateUser,user.getUserId());
        PageResult<Knowledge> knowledgePageResult = selectPage(knowledgeListDTO, lambdaQueryWrapper);
        return knowledgePageResult.convert(knowledgeConverter::entityToKnowledgeListVO);
    }

    /**
     * 根据父节点id查询当前路径
     * @param parentId 父id
     * @return 当前路径
     */
    @Override
    public List<KnowledgePathVO> listPathByParentId(Long parentId) {
        //1.查询父id是否存在
        if (parentId != null && !checkIsExistById(parentId)) {
            return null;
        }
        Knowledge knowledgeById = getKnowledgeById(parentId);
        if (ObjectUtil.isNull(knowledgeById)) {
            return null;
        }
        List<Long> pathId = knowledgeById.getPathId();
        if (ObjectUtil.isNull(pathId)) {
            List<KnowledgePathVO> result = new ArrayList<>();
            result.add(knowledgeConverter.entityToPathVO(knowledgeById));
            return result;
        }
        List<Knowledge> knowledgeList = getKnowledgeListByIds(pathId);
        if(ObjectUtil.isEmpty(knowledgeList)){
            return null;
        }
        List<KnowledgePathVO> result = knowledgeConverter.entityListToPathVOList(knowledgeList);
        result.add(knowledgeConverter.entityToPathVO(knowledgeById));
        return result;
    }

    /**
     * 移动文件或文件夹
     * @param knowledgeMoveDTO 移动文件或文件夹参数
     * @return 移动结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean move(KnowledgeMoveDTO knowledgeMoveDTO) {
        //1.查询父id是否存在
        Long parentId = knowledgeMoveDTO.getParentId();
        if (parentId != null && !checkIsExistById(parentId)) {
            throw new ServiceException(ChatResultCode.PARENT_ID_NOT_EXIST);
        }
        //2.查询当前id是否存在
        Long id = knowledgeMoveDTO.getId();
        if (!checkIsExistById(id)) {
            throw new ServiceException(ChatResultCode.ID_NOT_EXIST);
        }
        //3.查询当前id是否是父id
        Knowledge knowledgeById = getKnowledgeById(id);
        if (knowledgeById.getParentId().equals(parentId)) {
            return true;
        }
        Knowledge knowledgeByParentId = getKnowledgeById(parentId);
        List<Long> newPath = knowledgeByParentId.getPathId();
        if(ObjectUtil.isNull(newPath)){
            newPath = new ArrayList<>();
        }
        newPath.add(parentId);
        knowledgeById.setParentId(parentId);
        knowledgeById.setPathId(newPath);
        //修改当目录及其子目录的路径
        List<Knowledge> allChildren = getAllChildren(id);
        for (Knowledge knowledge : allChildren) {
            List<Long> pathId = knowledge.getPathId();
            //替换旧路径
            replayOldPath(pathId,id,newPath);
            knowledge.setPathId(pathId);
        }
        allChildren.add(knowledgeById);

        return updateBatchById(allChildren);
    }

    /**
     * 删除文件或文件夹
     * @param knowledgeIds 文件或文件夹id
     * @return 删除结果
     */
    @Override
    public boolean delete(String knowledgeIds) {
        List<Long> ids = StringUtil.splitToLong(knowledgeIds, StrUtil.COMMA);
        if(ObjectUtil.isEmpty(ids)){
            return false;
        }
        for (Long id : ids) {
            //1.查询当前id是否存在
            Knowledge knowledgeById = getKnowledgeById(id);
            if (ObjectUtil.isNull(knowledgeById)) {
                return true;
            }
            //2.查询是否有子节点
            if(checkIsChildById(id)){
                throw new ServiceException(ChatResultCode.HAS_CHILD);
            }
            //3.如果是文件查询是否有文件
            if(knowledgeDetailService.checkHasFile(id)){
                throw new ServiceException(ChatResultCode.HAS_FILE);
            }
        }
        //4.删除
        return removeByIds(ids);
    }

    /**
     * 文件处理
     * @param knowledgeProcessDTO 文件处理参数
     * @return 文件处理结果
     */
    @Override
    public boolean process(KnowledgeProcessDTO knowledgeProcessDTO) {
        //1.查询当前id是否存在
        Knowledge knowledgeById = getProcessKnowledge(knowledgeProcessDTO.getId());
        String fileName = knowledgeProcessDTO.getFileName();
        if(knowledgeDetailService.checkIsFileExist(knowledgeProcessDTO.getId(),fileName)){
            throw new ServiceException(ChatResultCode.FILE_EXIST);
        }
        //2.获取文件后缀名
        String fileNameSuffix = getFileNameSuffix(fileName);
        //3.判断是否txt,md,pdf
        if(!checkIsTxtMdPdf(fileNameSuffix)){
            throw new ServiceException(ChatResultCode.FILE_TYPE_ERROR);
        }
        //5.保存文件信息
        KnowledgeDetail knowledgeDetail = new KnowledgeDetail();
        knowledgeDetail.setKnowledgeId(knowledgeProcessDTO.getId());
        knowledgeDetail.setName(fileName);
        knowledgeDetail.setFileUrl(knowledgeProcessDTO.getFileUrl());
        knowledgeDetail.setStatus(KnowledgeConstant.NOT_STARTED);
        knowledgeDetail.setType(fileNameSuffix);
        boolean save = knowledgeDetailService.save(knowledgeDetail);
        //6.推送流程任务处理
        KnowledgeProcessEvent knowledgeProcessEvent = new KnowledgeProcessEvent(knowledgeById.getIndexName(),knowledgeDetail);
        applicationEventPublisher.publishEvent(knowledgeProcessEvent);
        return save;
    }


    /**
     * 文件内容上传处理(多文件处理)
     * @param knowledgeProcessBatchDTO
     * @return
     */
    @Override
    public boolean processBatch(KnowledgeProcessBatchDTO knowledgeProcessBatchDTO) {
        Knowledge processKnowledge = getProcessKnowledge(knowledgeProcessBatchDTO.getId());
        List<KnowledgeProcessBatchDTO.FileVO> files = knowledgeProcessBatchDTO.getFiles();
        if(ObjectUtil.isEmpty(files)){
            throw new ServiceException(ChatResultCode.FILE_NOT_EXIST);
        }
        //1.批量检验文件是否存在
        List<String> fileNames = files.stream().map(KnowledgeProcessBatchDTO.FileVO::getFileName).collect(Collectors.toList());
        if(knowledgeDetailService.checkIsFileBatchExist(knowledgeProcessBatchDTO.getId(),fileNames)){
            throw new ServiceException(ChatResultCode.FILE_EXIST);
        }
        //2.批量检验文件类型
        for (KnowledgeProcessBatchDTO.FileVO file : files) {
            String fileNameSuffix = getFileNameSuffix(file.getFileName());
            if(!checkIsTxtMdPdf(fileNameSuffix)){
                throw new ServiceException(ChatResultCode.FILE_TYPE_ERROR);
            }
        }
        //3.批量保存文件信息
        List<KnowledgeDetail> knowledgeDetails = new ArrayList<>();
        for (KnowledgeProcessBatchDTO.FileVO file : files) {
            KnowledgeDetail knowledgeDetail = new KnowledgeDetail();
            knowledgeDetail.setKnowledgeId(knowledgeProcessBatchDTO.getId());
            knowledgeDetail.setName(file.getFileName());
            knowledgeDetail.setFileUrl(file.getFileUrl());
            knowledgeDetail.setStatus(KnowledgeConstant.NOT_STARTED);
            knowledgeDetail.setType(getFileNameSuffix(file.getFileName()));
            knowledgeDetails.add(knowledgeDetail);
        }
        boolean saveBatch = knowledgeDetailService.saveBatch(knowledgeDetails);
        //4.推送流程任务处理
        KnowledgeProcessEvent knowledgeProcessEvent = new KnowledgeProcessEvent(processKnowledge.getIndexName(),knowledgeDetails);
        applicationEventPublisher.publishEvent(knowledgeProcessEvent);
        return saveBatch;
    }


    /**
     * 文件详情
     * @param id 文件id
     * @return 文件详情
     */
    @Override
    public KnowledgeFileListVO detailFile(Long id) {
        //1.查询当前id是否存在
        Knowledge knowledgeById = getKnowledgeById(id);
        if(ObjectUtil.isNull(knowledgeById)){
            return null;
        }
        if(!knowledgeById.getType().equals(KnowledgeConstant.FILE)){
            return null;
        }
        return knowledgeConverter.entityToKnowledgeFileVO(knowledgeById,knowledgeDetailService.listByKnowledgeId(id));
    }

    /**
     * 文件处理状态详情列表
     * @return 文件处理状态详情列表
     */
    @Override
    public List<KnowledgeFileVO> listFileStatus() {
        //查询未完成的文件
        List<KnowledgeDetail> listNotCompleted =  knowledgeDetailService.listNotCompleted();
        if(ObjectUtil.isEmpty(listNotCompleted)){
            return null;
        }
        List<Long> knowledgeIds = listNotCompleted.stream().map(KnowledgeDetail::getKnowledgeId).collect(Collectors.toList());
        List<Knowledge> knowledgeList = getKnowledgeListByIds(knowledgeIds);
        return knowledgeConverter.entityListToKnowledgeFileVOList(listNotCompleted,knowledgeList);
    }

    @Override
    public List<KnowledgeFileVO> listFile(Long knowledgeId) {
        //1.查询当前id是否存在
        Knowledge knowledgeById = getKnowledgeById(knowledgeId);
        if(ObjectUtil.isNull(knowledgeById)){
            return null;
        }
        if(!knowledgeById.getType().equals(KnowledgeConstant.FILE)){
            return null;
        }
        List<KnowledgeDetail> knowledgeDetails = knowledgeDetailService.listByKnowledgeId(knowledgeId);
        return knowledgeConverter.entityListToKnowledgeFileVOList(knowledgeDetails,knowledgeById);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(String knowledgeIds) {
        List<Long> ids = StringUtil.splitToLong(knowledgeIds, StrUtil.COMMA);
        List<Knowledge> knowledgeList = getKnowledgeFileList(ids);
        if(ObjectUtil.isEmpty(knowledgeList)){
            return true;
        }
        List<Long> knowledgeIdList = knowledgeList.stream().map(Knowledge::getId).collect(Collectors.toList());
        List<String> indexName = knowledgeList.stream().map(Knowledge::getIndexName).collect(Collectors.toList());
        //1.删除文件
        boolean flag = knowledgeDetailService.deleteByKnowledgeIds(knowledgeIdList);
        //2.删除索引
        if(!elasticOperation.deleteIndex(indexName)){
            throw new ServiceException(ChatResultCode.DELETE_INDEX_ERROR);
        }
        return flag;
    }

    @Override
    public boolean checkIsFile(Long knowledgeId) {
        return this.count(new LambdaQueryWrapper<>(Knowledge.class)
                .eq(Knowledge::getId,knowledgeId)
                .eq(Knowledge::getType,KnowledgeConstant.FILE)) > 0;
    }

    @Override
    public KnowledgeVO updateByDto(KnowledgeUpdateDTO updateDTO) {
        Knowledge knowledge = getById(updateDTO.getId());
        if(ObjectUtil.isNull(knowledge)){
            throw new ServiceException(ChatResultCode.ID_NOT_EXIST);
        }
        if(ObjectUtil.isNotEmpty(updateDTO.getName())){
            knowledge.setName(updateDTO.getName());
        }
        if(knowledge.getType().equals(KnowledgeConstant.FILE)){
            if(ObjectUtil.isNotEmpty(updateDTO.getImageUrl())){
                knowledge.setImageUrl(updateDTO.getImageUrl());
            }
            if(ObjectUtil.isNotEmpty(updateDTO.getMinScore())){
                knowledge.setMinScore(updateDTO.getMinScore());
            }
            if(ObjectUtil.isNotEmpty(updateDTO.getMaxResult())){
                knowledge.setMaxResult(updateDTO.getMaxResult());
            }
        }
        return updateById(knowledge) ? knowledgeConverter.entityToVO(knowledge) : null;
    }


    @Override
    public boolean reprocess(Long knowledgeDetailId) {
        //1.查询当前id是否存在
        KnowledgeDetail knowledgeDetailById = knowledgeDetailService.getById(knowledgeDetailId);
        Knowledge knowledgeById = getKnowledgeById(knowledgeDetailById.getKnowledgeId());
        if(ObjectUtil.isNull(knowledgeDetailById)){
            return true;
        }
        if(!knowledgeDetailById.getStatus().equals(KnowledgeConstant.FAIL)){
            return true;
        }
        knowledgeDetailById.setStatus(KnowledgeConstant.NOT_STARTED);
        //2.推送流程任务处理
        KnowledgeProcessEvent knowledgeProcessEvent = new KnowledgeProcessEvent(knowledgeById.getIndexName(),knowledgeDetailById);
        applicationEventPublisher.publishEvent(knowledgeProcessEvent);
        knowledgeDetailService.saveOrUpdate(knowledgeDetailById);
        return true;
    }

    /**
     * 生成索引名
     * @return 索引名
     */
    private String mkIndexName() {
        return UUID.fastUUID().toString();
    }

    /**
     * 判断是否txt,md,pdf
     * @param fileNameSuffix 文件后缀名
     * @return 是否txt,md,pdf
     */
    private boolean checkIsTxtMdPdf(String fileNameSuffix) {
        return "txt".equals(fileNameSuffix) || "md".equals(fileNameSuffix) || "pdf".equals(fileNameSuffix);
    }


    /**
     * 判断是否txt,md,pdf
     * @param fileName 文件
     * @return 是否txt,md,pdf
     */
    private String getFileNameSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 重放旧路径
     * @param pathId 旧路径
     * @param id 当前id
     * @param newPath 新路径
     */
    private void replayOldPath(List<Long> pathId, Long id, List<Long> newPath) {
        if (pathId == null || newPath == null) {
            return;
        }

        // 找到 id 在 pathId 中的索引，不包括 id
        int index = pathId.indexOf(id);
        if (index == -1) {
            return;
        }

        // 移除旧路径直到该索引（不包括该索引）
        for (int i = 0; i < index; i++) {
            if (!pathId.isEmpty()) {
                pathId.remove(0);
            }
        }

        // 在开头添加新路径
        for (int i = newPath.size() - 1; i >= 0; i--) {
            pathId.add(0, newPath.get(i));
        }
    }

    /**
     * 检测文件是否存在
     * @param id 文件id
     * @return 是否存在
     */
    private Boolean checkIsExistById(Long id) {
        return this.count(new LambdaQueryWrapper<Knowledge>().eq(Knowledge::getId, id)) > 0;
    }

    /**
     * 检测文件是否存在子节点
     * @param id 文件id
     * @return 是否存在
     */
    private Boolean checkIsChildById(Long id) {
        return this.count(new LambdaQueryWrapper<Knowledge>().eq(Knowledge::getParentId, id)) > 0;
    }

    /**
     * 根据id查询文件
     * @param id 文件id
     * @return 文件
     */
    private Knowledge getKnowledgeById(Long id) {
        return this.getOne(new LambdaQueryWrapper<Knowledge>().eq(Knowledge::getId, id));
    }

    /**
     * 根据id查询文件列表
     * @param ids 文件id
     * @return 文件
     */
    private List<Knowledge> getKnowledgeListByIds(List<Long> ids) {
        List<Knowledge> unsortedKnowledgeList  = this.list(new LambdaQueryWrapper<Knowledge>().in(Knowledge::getId, ids));
        if(ObjectUtil.isEmpty(unsortedKnowledgeList)){
            return null;
        }
        // 创建一个ID到知识对象的映射
        Map<Long, Knowledge> knowledgeMap = unsortedKnowledgeList.stream()
                .collect(Collectors.toMap(Knowledge::getId, Function.identity()));
        return ids.stream()
                .map(knowledgeMap::get)
                .filter(ObjectUtil::isNotEmpty) // 过滤掉任何在数据库中找不到的ID对应的null值
                .collect(Collectors.toList());
    }

    /**
     * 查询所有子节点
     * @param parentId 父节点id
     * @return 所有子节点
     */
    private List<Knowledge> getAllChildren(Long parentId) {
        List<Knowledge> result = new ArrayList<>();
        List<Knowledge> knowledgeList = this.list(new LambdaQueryWrapper<Knowledge>().eq(Knowledge::getParentId, parentId));
        if (ObjectUtil.isNotEmpty(knowledgeList)) {
            result.addAll(knowledgeList);
            for (Knowledge knowledge : knowledgeList) {
                result.addAll(getAllChildren(knowledge.getId()));
            }
        }
        return result;
    }


    /**
     * 文件处理-获取文件
     * @param id 文件知识库id
     * @return
     */
    private Knowledge getProcessKnowledge(Long id) {
        if(ObjectUtil.isNull(id)){
            throw new ServiceException(ChatResultCode.ID_NOT_EXIST);
        }
        Knowledge knowledgeById = getKnowledgeById(id);
        if(ObjectUtil.isNull(knowledgeById)){
            throw new ServiceException(ChatResultCode.ID_NOT_EXIST);
        }
        if(!knowledgeById.getType().equals(KnowledgeConstant.FILE)){
            throw new ServiceException(ChatResultCode.TYPE_NOT_FILE);
        }
        return knowledgeById;
    }

    /**
     * 获取知识库文件列表
     * @param ids
     * @return
     */
    private List<Knowledge> getKnowledgeFileList(List<Long> ids) {
        List<Knowledge> knowledgeListByIds = getKnowledgeListByIds(ids);
        if(ObjectUtil.isEmpty(knowledgeListByIds)){
            return null;
        }
        List<Knowledge> collect = knowledgeListByIds.stream().filter(knowledge -> knowledge.getType().equals(KnowledgeConstant.FILE)).collect(Collectors.toList());
        if(ObjectUtil.isEmpty(collect)){
            return null;
        }
        return collect;
    }
}
