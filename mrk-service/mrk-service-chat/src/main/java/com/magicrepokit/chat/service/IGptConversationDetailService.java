package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.GptChatDTO;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.mp.base.BaseService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface IGptConversationDetailService extends BaseService<GptConversationDetail> {
}
