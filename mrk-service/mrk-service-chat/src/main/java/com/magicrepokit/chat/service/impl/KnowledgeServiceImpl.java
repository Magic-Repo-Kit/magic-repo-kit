package com.magicrepokit.chat.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.converter.KnowledgeConverter;
import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.dto.KnowledgeListDTO;
import com.magicrepokit.chat.dto.KnowledgeMoveDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.mapper.KnowledgeMapper;
import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.chat.vo.KnowledgeListVO;
import com.magicrepokit.chat.vo.KnowledgePathVO;
import com.magicrepokit.chat.vo.KnowledgeVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mb.base.BaseServiceImpl;
import com.magicrepokit.oss.OssTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class KnowledgeServiceImpl extends BaseServiceImpl<KnowledgeMapper, Knowledge> implements IKnowledgeService {
    private final KnowledgeConverter knowledgeConverter;

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
        this.save(knowledge);


        return knowledgeConverter.entityToVO(knowledge);
    }

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
        List<Knowledge> knowledgeList = list(lambdaQueryWrapper);
        return knowledgeConverter.entityListToVOList(knowledgeList);
    }

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

    private Boolean checkIsExistById(Long id) {
        return this.count(new LambdaQueryWrapper<Knowledge>().eq(Knowledge::getId, id)) > 0;
    }

    private Knowledge getKnowledgeById(Long id) {
        return this.getOne(new LambdaQueryWrapper<Knowledge>().eq(Knowledge::getId, id));
    }

    private List<Knowledge> getKnowledgeListByIds(List<Long> ids) {
        return this.list(new LambdaQueryWrapper<Knowledge>().in(Knowledge::getId, ids));
    }

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
