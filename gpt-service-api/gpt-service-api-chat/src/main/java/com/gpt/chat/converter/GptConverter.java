package com.gpt.chat.converter;

import cn.hutool.core.util.ObjectUtil;
import com.gpt.chat.vo.gpt.GptConversationPage;
import com.gpt.chat.entity.GptConversation;
import com.gpt.chat.entity.GptConversationDetail;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GptConverter {

    GptConversationPage entity2Page(GptConversation gptConversation);

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
