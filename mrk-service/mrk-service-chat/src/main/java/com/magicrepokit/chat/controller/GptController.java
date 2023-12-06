package com.magicrepokit.chat.controller;

import com.magicrepokit.chat.dto.GptChatDTO;
import com.magicrepokit.chat.service.IGptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
}
