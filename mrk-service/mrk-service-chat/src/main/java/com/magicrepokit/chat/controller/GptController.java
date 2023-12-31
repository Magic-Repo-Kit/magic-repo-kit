package com.magicrepokit.chat.controller;

import com.magicrepokit.chat.dto.GptChatDTO;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.service.IGptService;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.api.R;
import com.magicrepokit.mb.base.PageParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "GPT聊天接口", tags = "GPT聊天接口")
public class GptController {
    private IGptService gptService;


    /**
     * gpt普通会话聊天
     * @param gptChatDTO
     * @return
     */
    @CrossOrigin("*")
    @PostMapping(path = "/chat", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @ApiOperation(value = "gpt聊天", notes = "gpt聊天[返回:text/event-stream]")
    public SseEmitter chat(@RequestBody GptChatDTO gptChatDTO) {
        return gptService.chat(gptChatDTO);
    }

    /**
     * 分页查询会话列表
     */
    @GetMapping("/list-conversation-by-page")
    @ApiOperation(value = "gpt会话分页", notes = "gpt会话分页")
    public R<PageResult<GptConversation>> listConversationByPage(PageParam pageParam){
        return R.data(gptService.listConversationByPage(pageParam));
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
