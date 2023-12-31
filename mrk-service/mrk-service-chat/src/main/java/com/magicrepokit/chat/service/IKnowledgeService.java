package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.GptTokenGetDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.entity.UserGpt;
import com.magicrepokit.mb.base.BaseService;
import org.springframework.web.multipart.MultipartFile;

public interface IKnowledgeService extends BaseService<Knowledge> {
    boolean uploadFileSplit(MultipartFile file);
}
