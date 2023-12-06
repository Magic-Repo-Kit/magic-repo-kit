package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.GptChatDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface IGptService {
    SseEmitter chat(GptChatDTO gptChatDTO);
}
