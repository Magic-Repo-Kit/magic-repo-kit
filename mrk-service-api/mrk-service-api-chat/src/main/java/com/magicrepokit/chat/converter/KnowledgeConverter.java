package com.magicrepokit.chat.converter;

import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.vo.KnowledgeVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KnowledgeConverter {

    Knowledge createDTOToEntity(KnowledgeCreateDTO createDTO);

    KnowledgeVO entityToVO(Knowledge knowledge);
}
