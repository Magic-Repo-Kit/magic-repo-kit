package com.gpt.chat.converter;

import com.gpt.chat.vo.gptRole.GptRolePageVO;
import com.gpt.chat.vo.gptRole.GptRoleVO;
import com.gpt.chat.dto.gptRole.GptRoleCreateDTO;
import com.gpt.chat.dto.gptRole.GptRoleUpdateDTO;
import com.gpt.chat.entity.GptRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GptRoleConverter {
    GptRole createDTO2Entity(GptRoleCreateDTO createDTO);

    GptRoleVO entity2VO(GptRole gptRole);

    GptRole updateDTO2Entity(GptRoleUpdateDTO updateDTO);

    GptRolePageVO entity2PageVO(GptRole gptRole);

    List<GptRoleVO> entityList2VOList(List<GptRole> gptRoles);

    List<GptRolePageVO> entityList2PageListVO(List<GptRole> gptRoles);
}
