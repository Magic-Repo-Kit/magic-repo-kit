package com.magicrepokit.chat.converter;

import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptRole;
import com.magicrepokit.chat.vo.gpt.GptConversationPage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GptConverter {

    GptConversationPage entity2Page(GptConversation gptConversation);

    default GptConversationPage entity2Page(GptConversation gptConversation, GptRole gptRole) {
        GptConversationPage gptConversationPage = entity2Page(gptConversation);
        if (ObjectUtil.isNotEmpty(gptRole)){
            gptConversationPage.setGptRoleId(gptRole.getId());
            gptConversationPage.setGptRoleName(gptRole.getName());
            gptConversationPage.setGptRoleImageUrl(gptRole.getImageUrl());
        }
        return gptConversationPage;
    }
}
