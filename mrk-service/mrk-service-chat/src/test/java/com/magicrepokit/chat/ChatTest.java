package com.magicrepokit.chat;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicrepokit.chat.dto.ChatMessagePagesDTO;
import com.magicrepokit.chat.dto.ChatMessageResponseDTO;
import com.magicrepokit.chat.service.IConversationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ChatTest {
    @Autowired
    private IConversationService conversationService;

    @Test
    public void StartDemo(){
        conversationService.sendMsg(UUID.fastUUID().toString(),"14445开4次根是多少",null,UUID.fastUUID().toString());
    }
//    "\\u5f53\\u7136\\u53ef\\u4ee5\\u3002\\u4ee5\\u4e0b\\u662f\\u4e00\\u4e2a\\u7b80\\u5355\\u7684\\u8ba1\\u7b97\\u5668\\u7684 JavaScript \\u4ee3\\u7801\\u793a\\u4f8b\\uff0c\\u8be5\\u4ee3\\u7801\\u53ef\\u4ee5\\u6267\\u884c\\u57fa\\u672c\\u7684\\u52a0\\u6cd5\\u3001\\u51cf\\u6cd5\\u3001\\u4e58\\u6cd5\\u548c\\u9664\\u6cd5\\u64cd\\u4f5c\\uff1a\\n\\n```html\\n<!DOCTYPE html>\\n<html lang=\\""

//    "\\u5f53\\u7136\\u53ef\\u4ee5\\u3002\\u4ee5\\u4e0b\\u662f\\u4e00\\u4e2a\\u7b80\\u5355\\u7684\\u8ba1\\u7b97\\u5668\\u7684 JavaScript \\u4ee3\\u7801\\u793a\\u4f8b\\uff0c\\u8be5\\u4ee3\\u7801\\u53ef\\u4ee5\\u6267\\u884c\\u57fa\\u672c\\u7684\\u52a0\\u6cd5\\u3001\\u51cf\\u6cd5\\u3001\\u4e58\\u6cd5\\u548c\\u9664\\u6cd5\\u64cd\\u4f5c\\uff1a\\n\\n```html\\n<!DOCTYPE html>\\n<html lang"
    @Test
    public void parseJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"message\": {\"id\": \"c6b92e60-ef4f-4b93-bdce-8bf27cd3c39a\", \"author\": {\"role\": \"assistant\", \"name\": null, \"metadata\": {}}, \"create_time\": 1700731853.285842, \"update_time\": null, \"content\": {\"content_type\": \"text\", \"parts\": " +
                "[\"html\\n<!DOCTYPE html>\\n<html lang=\\\\\"\"]}" +
                ", \"status\": \"in_progress\", \"end_turn\": null, \"weight\": 1.0, \"metadata\": {\"message_type\": \"next\", \"model_slug\": \"text-davinci-002-render-sha\", \"parent_id\": \"b8285e9a-b542-4bae-8747-0b72482d2354\"}, \"recipient\": \"all\"}, \"conversation_id\": \"259c39dd-d328-4805-951c-18799da9ef9f\", \"error\": null}";
        //ChatMessageResponseEntity bean = JSONUtil.toBean(json, ChatMessageResponseEntity.class);
        json = json.replaceAll("\\\\\"", "\"");
        ChatMessageResponseDTO bean = null;
        try {
            bean = objectMapper.readValue(json, ChatMessageResponseDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(bean);
    }

    @Test
    public void conversationList(){
        ChatMessagePagesDTO chatMessagePagesDTO = conversationService.conversationList(0, 10);
        System.out.println(chatMessagePagesDTO.toString());
    }
}
