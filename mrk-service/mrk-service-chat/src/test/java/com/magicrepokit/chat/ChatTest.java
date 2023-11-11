package com.magicrepokit.chat;

import cn.hutool.core.lang.UUID;
import com.magicrepokit.chat.entity.ChatMessagePages;
import com.magicrepokit.chat.service.IConversationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ChatTest {
    @Autowired
    private IConversationService conversationService;

    @Test
    public void StartDemo(){
        conversationService.sendMsg(UUID.fastUUID().toString(),"14445开4次根是多少",null,UUID.fastUUID().toString());
    }

    @Test
    public void conversationList(){
        ChatMessagePages chatMessagePages = conversationService.conversationList(0, 10);
        System.out.println(chatMessagePages.toString());
    }
}
