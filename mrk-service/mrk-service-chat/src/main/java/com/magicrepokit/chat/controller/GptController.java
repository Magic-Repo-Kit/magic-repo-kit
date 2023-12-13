package com.magicrepokit.chat.controller;

import com.magicrepokit.chat.dto.GptChatDTO;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.service.IGptService;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.api.R;
import com.magicrepokit.mb.base.PageParam;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class GptController {
    private IGptService gptService;


    /**
     * gpt聊天
     * @param gptChatDTO
     * @return
     */
    @CrossOrigin("*")
    @PostMapping(path = "/chat", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter chat(@RequestBody GptChatDTO gptChatDTO) {
        return gptService.chat(gptChatDTO);
    }

    /**
     * 分页查询会话列表
     */
    @GetMapping("/list-conversation-by-page")
    public R<PageResult<GptConversation>> listConversationByPage(PageParam pageParam){
        return R.data(gptService.listConversationByPage(pageParam));
    }

    /**
     * 查询会话详情
     * @param conversationId
     * @return
     */
    @GetMapping("/list-conversation-detail")
    public R<List<GptConversationDetail>> listConversationDetail(String conversationId){
        return R.data(gptService.listConversationDetail(conversationId));
    }

    /**
     * 查询会话详情分页
     * @param conversationId
     * @return
     */
    @GetMapping("/list-conversation-detail-by-page")
    public R<PageResult<GptConversationDetail>> listConversationDetailByPage(PageParam pageParam, String conversationId){
        return R.data(gptService.listConversationDetailByPage(pageParam, conversationId));
    }
}
