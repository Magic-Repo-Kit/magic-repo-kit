package com.magicrepokit.chat.converter;

import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.entity.KnowledgeDetail;
import com.magicrepokit.chat.vo.KnowledgeFileVO;
import com.magicrepokit.chat.vo.KnowledgeListVO;
import com.magicrepokit.chat.vo.KnowledgePathVO;
import com.magicrepokit.chat.vo.KnowledgeVO;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    KnowledgeFileVO entityToKnowledgeFileVO(Knowledge knowledge);

    @Mappings({
            @Mapping(source = "knowledgeDetail.knowledgeId", target = "id"),
            @Mapping(source = "knowledgeDetail.id", target = "detailId"),
            @Mapping(source = "knowledgeDetail.name", target = "fileName"),
            @Mapping(source = "knowledgeDetail.type", target = "fileType"),
            @Mapping(source = "knowledgeDetail.fileUrl", target = "fileUrl"),
            @Mapping(source = "knowledgeDetail.status", target = "status"),
            @Mapping(source = "knowledgeDetail.errorMsg", target = "errorMsg"),
            @Mapping(source = "knowledgeDetail.createTime", target = "createTime")
    })
    KnowledgeFileVO entityToKnowledgeFileVO(KnowledgeDetail knowledgeDetail);

    List<KnowledgeFileVO> entityListToKnowledgeFileVOList(List<KnowledgeDetail> knowledgeDetails);


    default List<KnowledgeFileVO> entityListToKnowledgeFileVOList( List<KnowledgeDetail> knowledgeDetails,Knowledge knowledge){
        List<KnowledgeFileVO> knowledgeFileVOS = entityListToKnowledgeFileVOList(knowledgeDetails);
        if(ObjectUtil.isEmpty(knowledgeFileVOS)){
            return null;
        }
        if(ObjectUtil.isNotNull(knowledge)){
            knowledgeFileVOS.forEach(knowledgeFileVO -> {
                knowledgeFileVO.setName(knowledge.getName());
                knowledgeFileVO.setDescription(knowledge.getDescription());
                knowledgeFileVO.setIndexName(knowledge.getIndexName());
            });
        }
        return knowledgeFileVOS;
    }



    default KnowledgeFileVO entityToKnowledgeFileVO(Knowledge knowledge, KnowledgeDetail knowledgeDetail){
        KnowledgeFileVO knowledgeFileVO = entityToKnowledgeFileVO(knowledge);
        if(ObjectUtil.isNotNull(knowledgeDetail)){
            knowledgeFileVO.setDetailId(knowledgeDetail.getId());
            knowledgeFileVO.setFileName(knowledgeDetail.getName());
            knowledgeFileVO.setFileType(knowledgeDetail.getType());
            knowledgeFileVO.setFileUrl(knowledgeDetail.getFileUrl());
            knowledgeFileVO.setStatus(knowledgeDetail.getStatus());
            knowledgeFileVO.setErrorMsg(knowledgeDetail.getErrorMsg());
            knowledgeFileVO.setCreateTime(knowledgeDetail.getCreateTime());
        }
        return knowledgeFileVO;
    }

    default List<KnowledgeFileVO> entityListToKnowledgeFileVOList(List<KnowledgeDetail> listNotCompleted, List<Knowledge> knowledgeList){
        List<KnowledgeFileVO> knowledgeFileVOS = entityListToKnowledgeFileVOList(listNotCompleted);
        if(ObjectUtil.isEmpty(knowledgeFileVOS)){
            return null;
        }
        if(ObjectUtil.isNotEmpty(knowledgeList)) {
            Map<Long, Knowledge> knowledgeMap = knowledgeList.stream().collect(Collectors.toMap(Knowledge::getId, Knowledge -> Knowledge));
            knowledgeFileVOS.forEach(knowledgeFileVO -> {
                Knowledge knowledge = knowledgeMap.get(knowledgeFileVO.getId());
                if (ObjectUtil.isNotNull(knowledge)) {
                    knowledgeFileVO.setName(knowledge.getName());
                    knowledgeFileVO.setDescription(knowledge.getDescription());
                    knowledgeFileVO.setIndexName(knowledge.getIndexName());
                }
            });
        }
        return knowledgeFileVOS;
    }
}
