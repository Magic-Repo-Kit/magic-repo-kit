package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.gptRole.GptRoleCreateDTO;
import com.magicrepokit.chat.dto.gptRole.GptRolePageDTO;
import com.magicrepokit.chat.dto.gptRole.GptRoleUpdateDTO;
import com.magicrepokit.chat.entity.GptRole;
import com.magicrepokit.chat.vo.gptRole.GptRolePageVO;
import com.magicrepokit.chat.vo.gptRole.GptRoleVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.mb.base.BaseService;

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
}
