package com.magicrepokit.chat.converter;

import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.entity.GptRole;
import com.magicrepokit.chat.vo.gpt.GptConversationPage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

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

    default List<ChatMessage> conversationDetail2ChatMessage(List<GptConversationDetail> list){
        if(ObjectUtil.isEmpty(list)){
            return null;
        }
        return list.stream().map(item -> {
            if(item.getType()==1){
                return new UserMessage(item.getMessage());
            }else {
                return new AiMessage(item.getMessage());
            }
        }).collect(Collectors.toList());
    }
}
