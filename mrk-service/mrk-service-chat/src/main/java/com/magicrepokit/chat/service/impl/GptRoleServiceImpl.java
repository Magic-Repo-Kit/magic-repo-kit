package com.magicrepokit.chat.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.constant.GptModel;
import com.magicrepokit.chat.converter.GptRoleConverter;
import com.magicrepokit.chat.dto.gptRole.GptRoleCreateDTO;
import com.magicrepokit.chat.dto.gptRole.GptRolePageDTO;
import com.magicrepokit.chat.dto.gptRole.GptRoleUpdateDTO;
import com.magicrepokit.chat.entity.GptRole;
import com.magicrepokit.chat.mapper.GptRoleMapper;
import com.magicrepokit.chat.service.IGptRoleService;
import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.chat.vo.gptRole.GptRolePageVO;
import com.magicrepokit.chat.vo.gptRole.GptRoleVO;
import com.magicrepokit.chat.vo.knowledge.KnowledgeFileListVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.utils.AuthUtil;
import com.magicrepokit.common.utils.ObjectUtil;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mb.base.BaseServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@AllArgsConstructor
public class GptRoleServiceImpl extends BaseServiceImpl<GptRoleMapper, GptRole> implements IGptRoleService {
    private final GptRoleConverter gptRoleConverter;
    private final IKnowledgeService knowledgeService;

    @Override
    public GptRoleVO create(GptRoleCreateDTO createDTO) {
        GptRole gptRole = gptRoleConverter.createDTO2Entity(createDTO);
        checkKnowledge(gptRole.getKnowledgeId());
        checkModelName(gptRole.getModelName());
        this.save(gptRole);
        return gptRoleConverter.entity2VO(gptRole);
    }


    @Override
    public GptRoleVO updateByDto(GptRoleUpdateDTO updateDTO) {
        GptRole gptRole = gptRoleConverter.updateDTO2Entity(updateDTO);
        if (!checkIsExist(gptRole.getId())) {
            throw new ServiceException(ChatResultCode.ID_NOT_EXIST);
        }
        checkKnowledge(gptRole.getKnowledgeId());
        checkModelName(gptRole.getModelName());
        //TODO 后期上角色权限
        this.update(gptRole, new LambdaQueryWrapper<GptRole>()
                .eq(GptRole::getId, gptRole.getId())
                .eq(GptRole::getCreateUser, AuthUtil.getUser().getUserId()));
        return gptRoleConverter.entity2VO(gptRole);
    }

    @Override
    public Boolean deleteById(Long id) {
        //TODO 后期上角色权限
        this.remove(new LambdaQueryWrapper<GptRole>()
                .eq(GptRole::getId, id)
                .eq(GptRole::getCreateUser, AuthUtil.getUser().getUserId()));
        return true;
    }

    @Override
    public GptRoleVO detailById(Long id) {
        GptRole gptRole = this.getById(id);
        if (ObjectUtil.isNotEmpty(gptRole)) {
            GptRoleVO gptRoleVO = gptRoleConverter.entity2VO(gptRole);
            if(ObjectUtil.isNotEmpty(gptRole.getKnowledgeId())){
                KnowledgeFileListVO knowledgeFileListVO = knowledgeService.detailFile(gptRole.getKnowledgeId());
                gptRoleVO.setKnowledgeFileListVO(knowledgeFileListVO);
            }
            return gptRoleVO;
        }
        return null;
    }

    @Override
    public PageResult<GptRolePageVO> page(GptRolePageDTO pageDTO) {
        LambdaQueryWrapper<GptRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //TODO 后期上角色权限
        lambdaQueryWrapper.eq(GptRole::getCreateUser, AuthUtil.getUser().getUserId());
        if (ObjectUtil.isNotEmpty(pageDTO.getKeywords())) {
            lambdaQueryWrapper.or()
                    .likeRight(GptRole::getDescription, pageDTO.getKeywords())
                    .or().
                    likeRight(GptRole::getName, pageDTO.getKeywords());
        }
        if(ObjectUtil.isNotEmpty(pageDTO.getModelName())){
            lambdaQueryWrapper.eq(GptRole::getModelName,pageDTO.getModelName());
        }
        PageResult<GptRole> gptRolePageResult = selectPage(pageDTO, lambdaQueryWrapper);
        PageResult<GptRolePageVO> convert = gptRolePageResult.convert(gptRoleConverter::entity2PageVO);
        List<GptRolePageVO> list = convert.getList();
        if(ObjectUtil.isEmpty(list)){
            convert.setList(getDefaultModel());
            return convert;
        }
        list.addAll(getDefaultModel());
        return convert;
    }

    @Override
    public Map<Long, GptRole> queryIdMap(List<Long> gptRoleIds) {
        List<GptRole> gptRoles = this.list(new LambdaQueryWrapper<GptRole>().in(GptRole::getId, gptRoleIds));
        if (ObjectUtil.isNotEmpty(gptRoles)) {
            return gptRoles.stream().collect(toMap(GptRole::getId, gptRole -> gptRole));
        }
        return null;
    }

    /**
     * 获取默认Gpt角色(3.5 4.0)
     */
    public List<GptRolePageVO> getDefaultModel() {
        List<GptRole> gptRoles = this.list(new LambdaQueryWrapper<GptRole>().eq(GptRole::getCreateUser, 0L));
        return gptRoleConverter.entityList2PageListVO(gptRoles);
    }

    /**
     * 检查是否存在
     *
     * @param id id
     * @return 是否存在
     */
    private boolean checkIsExist(Long id) {
        GptRole gptRole = this.getById(id);
        return ObjectUtil.isNotEmpty(gptRole);
    }

    /**
     * 检查知识库是否存在
     *
     * @param knowledgeId 知识库id
     */
    private void checkKnowledge(Long knowledgeId) {
        if (ObjectUtil.isNotEmpty(knowledgeId)) {
            if (!knowledgeService.checkIsFile(knowledgeId)) {
                throw new ServiceException(ChatResultCode.KNOWLEDGE_FILE_NOT_EXIST);
            }
        }
    }

    /**
     * 检查模型名称是否存在
     *
     * @param modelName 模型名称
     */
    private void checkModelName(String modelName) {
        if (ObjectUtil.isEmpty(modelName)
                || ObjectUtil.isEmpty(GptModel.getByModelName(modelName))
        ) {
            throw new ServiceException(ChatResultCode.GPT_MODEL_NAME_ERROR);
        }
    }
}
