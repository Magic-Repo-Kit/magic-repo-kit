package com.gpt.chat.controller;

import com.gpt.chat.dto.gpt.GptChatDTO;
import com.gpt.chat.dto.gpt.GptChatPresetDTO;
import com.gpt.chat.entity.GptConversationDetail;
import com.gpt.chat.service.IGptService;
import com.gpt.chat.vo.gpt.GptConversationPage;
import com.gpt.common.api.PageResult;
import com.gpt.common.api.R;
import com.gpt.mb.base.PageParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
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
@Api(value = "GPT聊天接口", tags = "GPT聊天接口")
public class GptController {
    private IGptService gptService;

    /**
     * gpt角色聊天
     * @param GptChatDTO
     */
    @PostMapping(path = "/chat-role", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @ApiOperation(value = "gpt知识库聊天", notes = "gpt知识库聊天[返回:text/event-stream]")
    public SseEmitter chatKnowledge(@RequestBody GptChatDTO GptChatDTO) {
        return gptService.chatRole(GptChatDTO);
    }

    /**
     * gpt聊天预设
     */
    @PostMapping(path = "/chat-preset", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @ApiOperation(value = "gpt聊天预设", notes = "gpt聊天预设[返回:text/event-stream]")
    public SseEmitter chatPreset(@RequestBody GptChatPresetDTO chatPresetDTO) {
        return gptService.chatPreset(chatPresetDTO);
    }


    /**
     * 分页查询会话列表
     */
    @GetMapping("/page-conversation")
    @ApiOperation(value = "gpt会话分页", notes = "gpt会话分页")
    public R<PageResult<GptConversationPage>> pageConversation(PageParam pageParam){
        return R.data(gptService.pageConversation(pageParam));
    }

    /**
     * 查询会话详情
     * @param conversationId
     * @return
     */
    @GetMapping("/list-conversation-detail")
    @ApiOperation(value = "gpt会话详情列表", notes = "gpt会话详情列表")
    public R<List<GptConversationDetail>> listConversationDetail(@ApiParam(value = "会话id",required = true) String conversationId){
        return R.data(gptService.listConversationDetail(conversationId));
    }

    /**
     * 查询会话详情分页
     * @param conversationId
     * @return
     */
    @GetMapping("/list-conversation-detail-by-page")
    @ApiOperation(value = "gpt会话详情分页", notes = "gpt会话详情分页")
    public R<PageResult<GptConversationDetail>> listConversationDetailByPage(PageParam pageParam,@ApiParam(value = "会话id",required = true) String conversationId){
        return R.data(gptService.listConversationDetailByPage(pageParam, conversationId));
    }
}
