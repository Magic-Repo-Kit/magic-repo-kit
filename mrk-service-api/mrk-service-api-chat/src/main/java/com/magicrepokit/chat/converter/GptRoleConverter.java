package com.magicrepokit.chat.converter;

import com.magicrepokit.chat.dto.gptRole.GptRoleCreateDTO;
import com.magicrepokit.chat.dto.gptRole.GptRoleUpdateDTO;
import com.magicrepokit.chat.entity.GptRole;
import com.magicrepokit.chat.vo.gptRole.GptRolePageVO;
import com.magicrepokit.chat.vo.gptRole.GptRoleVO;
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
