package com.magicrepokit.chat.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.constant.KnowledgeConstant;
import com.magicrepokit.chat.converter.KnowledgeConverter;
import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.dto.KnowledgeListDTO;
import com.magicrepokit.chat.dto.KnowledgeMoveDTO;
import com.magicrepokit.chat.dto.KnowledgeProcessDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.entity.KnowledgeDetail;
import com.magicrepokit.chat.event.KnowledgeProcessEvent;
import com.magicrepokit.chat.mapper.KnowledgeMapper;
import com.magicrepokit.chat.service.IKnowledgeDetailService;
import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.chat.vo.KnowledgeFileVO;
import com.magicrepokit.chat.vo.KnowledgeListVO;
import com.magicrepokit.chat.vo.KnowledgePathVO;
import com.magicrepokit.chat.vo.KnowledgeVO;
import com.magicrepokit.common.utils.AuthUtil;
import com.magicrepokit.jwt.entity.MRKUser;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mb.base.BaseServiceImpl;
import com.magicrepokit.oss.OssTemplate;
import com.magicrepokit.oss.model.MRKFile;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class KnowledgeServiceImpl extends BaseServiceImpl<KnowledgeMapper, Knowledge> implements IKnowledgeService {
    private final KnowledgeConverter knowledgeConverter;
    private final OssTemplate ossTemplate;
    private final IKnowledgeDetailService knowledgeDetailService;
    private ApplicationEventPublisher applicationEventPublisher;

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
    public List<KnowledgeListVO> list(KnowledgeListDTO knowledgeListDTO) {
        LambdaQueryWrapper<Knowledge> lambdaQueryWrapper = new LambdaQueryWrapper<Knowledge>();
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
        lambdaQueryWrapper.eq(Knowledge::getCreateUser,user.getUserId());
        List<Knowledge> knowledgeList = list(lambdaQueryWrapper);
        return knowledgeConverter.entityListToVOList(knowledgeList);
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
        List<KnowledgePathVO> result = new ArrayList<>();
        //倒叙
        for (int i = knowledgeList.size() - 1; i >= 0; i--) {
            result.add(knowledgeConverter.entityToPathVO(knowledgeList.get(i)));
        }
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
     * @param id 文件或文件夹id
     * @return 删除结果
     */
    @Override
    public boolean delete(Long id) {
        //1.查询当前id是否存在
        Knowledge knowledgeById = getKnowledgeById(id);
        if (ObjectUtil.isNull(knowledgeById)) {
            throw new ServiceException(ChatResultCode.ID_NOT_EXIST);
        }
        //2.查询是否有子节点
        if(checkIsChildById(id)){
            throw new ServiceException(ChatResultCode.HAS_CHILD);
        }
        //3.TODO 如果是文件查询是否有文件
        //4.删除
        return removeById(id);
    }

    /**
     * 文件处理
     * @param knowledgeProcessDTO 文件处理参数
     * @return 文件处理结果
     */
    @Override
    public boolean process(KnowledgeProcessDTO knowledgeProcessDTO) {
        //1.查询当前id是否存在
        Knowledge knowledgeById = getKnowledgeById(knowledgeProcessDTO.getId());
        if(ObjectUtil.isNull(knowledgeById)){
            throw new ServiceException(ChatResultCode.ID_NOT_EXIST);
        }
        if(!knowledgeById.getType().equals(KnowledgeConstant.FILE)){
            throw new ServiceException(ChatResultCode.TYPE_NOT_FILE);
        }
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
     * 文件详情
     * @param id 文件id
     * @return 文件详情
     */
    @Override
    public KnowledgeFileVO detailFile(Long id) {
        //1.查询当前id是否存在
        Knowledge knowledgeById = getKnowledgeById(id);
        if(ObjectUtil.isNull(knowledgeById)){
            return null;
        }
        if(!knowledgeById.getType().equals(KnowledgeConstant.FILE)){
            return null;
        }
        return knowledgeConverter.entityToKnowledgeFileVO(knowledgeById,getKnowledgeDetailByKnowledgeId(id));
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
        knowledgeDetailService.save(knowledgeDetailById);
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
        return this.list(new LambdaQueryWrapper<Knowledge>().in(Knowledge::getId, ids));
    }


    /**
     * 根据id查询文件详情
     * @param id 文件id
     * @return 文件详情
     */
    private KnowledgeDetail getKnowledgeDetailByKnowledgeId(Long id) {
        return knowledgeDetailService.getOne(new LambdaQueryWrapper<KnowledgeDetail>().eq(KnowledgeDetail::getKnowledgeId, id));
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
}
