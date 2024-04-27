package com.gpt.chat.service;

import com.gpt.chat.dto.gptRole.GptRoleCreateDTO;
import com.gpt.chat.dto.gptRole.GptRolePageDTO;
import com.gpt.chat.dto.gptRole.GptRoleUpdateDTO;
import com.gpt.chat.entity.GptRole;
import com.gpt.chat.vo.gptRole.GptRolePageVO;
import com.gpt.chat.vo.gptRole.GptRoleVO;
import com.gpt.common.api.PageResult;
import com.gpt.mb.base.BaseService;

import java.util.List;
import java.util.Map;

public interface IGptRoleService extends BaseService<GptRole> {

    /**
     * 建立Gpt角色
     * @param createDTO 建立Gpt角色DTO
     * @return Gpt角色VO
     */
    GptRoleVO create(GptRoleCreateDTO createDTO);

    /**
     * 修改Gpt角色
     * @param updateDTO 修改Gpt角色DTO
     * @return Gpt角色VO
     */
    GptRoleVO updateByDto(GptRoleUpdateDTO updateDTO);

    /**
     * 删除Gpt角色
     * @param id Gpt角色id
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    /**
     * 根据id获取Gpt角色
     * @param id
     * @return
     */
    GptRoleVO detailById(Long id);

    /**
     * Gpt角色列表分页
     * @param pageDTO 分页DTO
     * @return Gpt角色分页VO
     */
    PageResult<GptRolePageVO> page(GptRolePageDTO pageDTO);

    /**
     * id映射Gpt角色
     * @param gptRoleIds Gpt角色ids
     * @return
     */
    Map<Long, GptRole> queryIdMap(List<Long> gptRoleIds);
}
