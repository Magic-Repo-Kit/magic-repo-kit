package com.magicrepokit.chat.controller;

import com.magicrepokit.chat.dto.GptChatDTO;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.service.IGptService;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.api.R;
import com.magicrepokit.mb.base.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * GPT聊天
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("gpt")
public class GptController {
    @Autowired
    private IGptService gptService;


    @CrossOrigin("*")
    @PostMapping(path = "/chat", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter chat(@RequestBody GptChatDTO gptChatDTO) {
        return gptService.chat(gptChatDTO);
    }

    @GetMapping("/list-conversation-by-page")
    public R<PageResult<GptConversation>> listConversationByPage(PageParam pageParam){
        return R.data(gptService.listConversationByPage(pageParam));
    }

    @GetMapping("/list-conversation-detail")
    public R<List<GptConversationDetail>> listConversationDetail(String conversationId){
        return R.data(gptService.listConversationDetail(conversationId));
    }
}
