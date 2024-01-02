package com.magicrepokit.chat.converter;

import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.vo.KnowledgeListVO;
import com.magicrepokit.chat.vo.KnowledgePathVO;
import com.magicrepokit.chat.vo.KnowledgeVO;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KnowledgeConverter {

    Knowledge createDTOToEntity(KnowledgeCreateDTO createDTO);

    KnowledgeVO entityToVO(Knowledge knowledge);


    List<KnowledgeListVO> entityListToVOList(List<Knowledge> knowledges);


    List<KnowledgePathVO> entityListToPathVOList(List<Knowledge> knowledgeList);

    @Mappings({
            @Mapping(source = "knowledge.name", target = "parentName"),
            @Mapping(source = "knowledge.id", target = "parentId")
    })
    KnowledgePathVO entityToPathVO(Knowledge knowledge);
}
