package com.magicrepokit.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.converter.KnowledgeConverter;
import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.dto.KnowledgePageDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.mapper.KnowledgeMapper;
import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.chat.vo.KnowledgePageVO;
import com.magicrepokit.chat.vo.KnowledgeVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mb.base.BaseServiceImpl;
import com.magicrepokit.oss.OssTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KnowledgeServiceImpl extends BaseServiceImpl<KnowledgeMapper, Knowledge> implements IKnowledgeService {
    private OssTemplate ossTemplate;
    private KnowledgeConverter knowledgeConverter;

    @Override
    public KnowledgeVO create(KnowledgeCreateDTO createDTO) {
        //1.查询父id是否存在
        Long parentId = createDTO.getParentId();
        if(createDTO.getParentId()!=null&&!checkIsExistById(parentId)){
            throw new ServiceException(ChatResultCode.PARENT_ID_NOT_EXIST);
        }
        Knowledge knowledge = knowledgeConverter.createDTOToEntity(createDTO);
        this.save(knowledge);


        return knowledgeConverter.entityToVO(knowledge);
    }

    @Override
    public PageResult<KnowledgePageVO> page(KnowledgePageDTO knowledgePageDTO) {
        LambdaQueryWrapper<Knowledge> lambdaQueryWrapper = new LambdaQueryWrapper<Knowledge>();
        //1.id为空查询父一级
        if(ObjectUtil.isEmpty(knowledgePageDTO.getId())){
            lambdaQueryWrapper.eq(Knowledge::getParentId, 0);
        }else{
            lambdaQueryWrapper.eq(Knowledge::getParentId, knowledgePageDTO.getId());
        }
        //2.模糊查询
        if(ObjectUtil.isNotEmpty(knowledgePageDTO.getKeywords())){
            lambdaQueryWrapper.likeRight(Knowledge::getName, knowledgePageDTO.getKeywords());
        }
        PageResult<Knowledge> knowledgePageResult = selectPage(knowledgePageDTO, lambdaQueryWrapper);
        return null;
    }

    private Boolean checkIsExistById(Long id) {
        return this.count(new LambdaQueryWrapper<Knowledge>().eq(Knowledge::getId, id)) > 0;
    }
}
