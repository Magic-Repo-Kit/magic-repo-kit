package com.magicrepokit.chat.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.magicrepokit.chat.component.SseEmitterComponent;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.constant.StatusConstant;
import com.magicrepokit.chat.dto.GptChatDTO;
import com.magicrepokit.chat.dto.ChatMessageResponseDTO;
import com.magicrepokit.chat.dto.GptTokenGetDTO;
import com.magicrepokit.chat.service.IConversationService;
import com.magicrepokit.chat.service.IGptService;
import com.magicrepokit.chat.service.IUserGptService;
import com.magicrepokit.common.utils.AuthUtil;
import com.magicrepokit.jwt.entity.MRKUser;
import com.magicrepokit.log.exceotion.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

@Service
public class GptServiceImpl implements IGptService {
    @Autowired
    private SseEmitterComponent sseEmitterComponent;
    @Autowired
    private IConversationService conversationService;
    @Autowired
    private IUserGptService userGptService;

    @Override
    public SseEmitter chat(GptChatDTO gptChatDTO) {
        //获取用户信息
        MRKUser user = AuthUtil.getUser();
        if (user == null) {
            throw new ServiceException(ChatResultCode.NOT_AUTHORIZED);
        }
        //获取账户额度
        GptTokenGetDTO gptToken = userGptService.getGptToken(user.getUserId());
        if (gptToken.getStatus().equals(StatusConstant.GPT_NO_ACCOUNT)) {
            throw new ServiceException(ChatResultCode.GPT_NO_ACCOUNT);
        }
        if (gptToken.getStatus().equals(StatusConstant.GPT_NO_REGULAR_CREDIT_LIMIT)) {
            throw new ServiceException(ChatResultCode.GPT_NO_REGULAR_CREDIT_LIMIT);
        }
        //校验数据类型
        validateGptChatDTO(gptChatDTO);
        //建立连接
        String key = sseEmitterComponent.getKey(user.getUserId().toString(), user.getAccount());
        //获取连接
        SseEmitter sseEmitter = sseEmitterComponent.SseEmitterConnect(key);
        //推送消息
        conversationService.sendMsg(gptChatDTO.getMessageId(), gptChatDTO.getContent(), gptChatDTO.getConversationId(), gptChatDTO.getParentMessageId(), createConsumer(key));
        sseEmitterComponent.close(key);
        return sseEmitter;
    }

    /**
     * 校验数据
     *
     * @param gptChatDTO
     */
    private void validateGptChatDTO(GptChatDTO gptChatDTO) {
        //如果会话uuid不为空检测是否为uuid类型
        if (StrUtil.isNotBlank(gptChatDTO.getConversationId())) {
            if (!checkIsUUID(gptChatDTO.getConversationId())) {
                throw new ServiceException(ChatResultCode.CHAT_UUID_ERROR);
            }
        }
        //如果消息uuid不为空检测是否为uuid类型
        if (StrUtil.isNotBlank(gptChatDTO.getMessageId())) {
            if (!checkIsUUID(gptChatDTO.getMessageId())) {
                throw new ServiceException(ChatResultCode.CHAT_UUID_ERROR);
            }
        }
        //如果父消息uuid不为空检测是否为uuid类型
        if (StrUtil.isNotBlank(gptChatDTO.getParentMessageId())) {
            if (!checkIsUUID(gptChatDTO.getParentMessageId())) {
                throw new ServiceException(ChatResultCode.CHAT_UUID_ERROR);
            }
        }
    }

    private Boolean checkIsUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Consumer<InputStream> createConsumer(String key) {
        return new Consumer<InputStream>() {
            @Override
            public void accept(InputStream inputStream) {
                SSEClientSend(inputStream, key);
            }
        };
    }

    private void SSEClientSend(InputStream inputStream, String clientId) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String line;
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            while ((line = reader.readLine()) != null) {
                //特殊字符串解析
                line = line.replaceAll("\\\\\"", "\"");
                //line 去掉 data:
                if (StrUtil.isNotBlank(line) && line.startsWith("data:")) {
                    line = line.substring(5);
                    if (line.contains("[DONE]")) {
                        line = "{done:true}";
                    }
                }
                if (StrUtil.isNotBlank(line)) {
                    ChatMessageResponseDTO bean = JSONUtil.toBean(line, ChatMessageResponseDTO.class);
                    sseEmitterComponent.SseEmitterSendMessage(line, clientId);
                }
            }
        } catch (IOException e) {
            // 处理异常
        }
    }

}
