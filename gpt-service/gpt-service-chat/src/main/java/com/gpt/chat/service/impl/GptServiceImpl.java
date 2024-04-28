package com.gpt.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.gpt.chat.component.LangchainComponent;
import com.gpt.chat.component.SseEmitterComponent;
import com.gpt.chat.constant.ChatResultCode;
import com.gpt.chat.constant.GptModel;
import com.gpt.chat.constant.StatusConstant;
import com.gpt.chat.dto.gpt.GptChatDTO;
import com.gpt.chat.dto.gpt.GptChatPresetDTO;
import com.gpt.chat.dto.gpt.GptConversationPageDTO;
import com.gpt.chat.dto.gpt.GptSaveChatMessage;
import com.gpt.chat.entity.GptConversation;
import com.gpt.chat.entity.GptConversationDetail;
import com.gpt.chat.service.*;
import com.gpt.chat.vo.Error;
import com.gpt.chat.vo.gpt.GptConversationPage;
import com.gpt.chat.vo.gpt.GptSSEResponse;
import com.gpt.chat.vo.gptRole.GptRoleVO;
import com.gpt.chat.vo.knowledge.KnowledgeFileListVO;
import com.gpt.common.api.PageResult;
import com.gpt.common.utils.AuthUtil;
import com.gpt.jwt.entity.GPTUser;
import com.gpt.log.exceotion.ServiceException;
import com.gpt.mb.base.PageParam;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Service
@AllArgsConstructor
@Slf4j
public class GptServiceImpl implements IGptService {
    private final SseEmitterComponent sseEmitterComponent;
    private final IGptConversationService gptConversationService;
    private final LangchainComponent langchainComponent;

    /**
     * gpt角色聊天
     * @param gptChatDTO 聊天内容
     * @return SseEmitter
     */
    @Override
    public SseEmitter chatRole(GptChatDTO gptChatDTO) {
        boolean isNewConversation = false;
        if(ObjectUtil.isEmpty(gptChatDTO.getConversationId())){
            isNewConversation = true;
            gptChatDTO.setConversationId(UUID.fastUUID().toString());
        }
        //验证uuid
        checkIsUUID(gptChatDTO.getConversationId());
        //结果集设定
        GptSSEResponse gptSSEResponse = new GptSSEResponse();
        gptSSEResponse.setConversationId(gptChatDTO.getConversationId());
        gptSSEResponse.setGptRoleId(gptChatDTO.getRoleId());
        //获取连接
        GPTUser user = AuthUtil.getUser();
        Long userId = user.getUserId();
        String sseKey = getSSEKey();
        SseEmitter sseEmitter = sseEmitterComponent.SseEmitterConnect(sseKey);

        //建立模型
        StreamingChatLanguageModel streamingChatLanguageModel = langchainComponent.getLlamaChatModel(GptModel.LLAMA2);
        List<ChatMessage> chatMessages = new ArrayList<>();

        //1.历史记忆
        if(ObjectUtil.isNotEmpty(gptChatDTO.getIsContext())&&gptChatDTO.getIsContext().equals(StatusConstant.YES)){
            List<ChatMessage> historyMessage = getHistoryMessage(gptChatDTO.getConversationId(),gptChatDTO.getContextLength());
            if(CollUtil.isNotEmpty(historyMessage)){
                chatMessages.addAll(historyMessage);
            }
        }

        //5.用户问题
        UserMessage userMessage = new UserMessage(gptChatDTO.getContent());
        chatMessages.add(userMessage);

        boolean finalIsNewConversation = isNewConversation;
        streamingChatLanguageModel.generate(chatMessages, new StreamingResponseHandler<AiMessage>() {
            @Override
            @Async
            public void onNext(String token) {
                gptSSEResponse.setMessage(token);
                gptSSEResponse.setError(null);
                gptSSEResponse.setIsEnd(false);
                sseEmitterComponent.SseEmitterSendMessage(gptSSEResponse, sseKey);
            }

            @Override
            @Async
            public void onError(Throwable error) {
                Error resError = new Error();
                resError.setCode(ChatResultCode.CHAT_ERROR.getCode()+"");
                resError.setMessage(error.getMessage());
                gptSSEResponse.setError(resError);
                gptSSEResponse.setIsEnd(true);
                sseEmitterComponent.SseEmitterSendMessage(gptSSEResponse, sseKey);
                sseEmitterComponent.close(sseKey);
                log.error("聊天异常:",error);
            }

            @Override
            @Async
            public void onComplete(Response<AiMessage> response) {
                StreamingResponseHandler.super.onComplete(response);
                gptSSEResponse.setError(null);
                gptSSEResponse.setMessage(response.content().text());
                gptSSEResponse.setIsEnd(true);
                sseEmitterComponent.SseEmitterSendMessage(gptSSEResponse, sseKey);
                sseEmitterComponent.close(sseKey);
                //保存聊天记录
                saveChatHistory(gptChatDTO.getConversationId(),gptChatDTO.getRoleId(),response.content().text(),userId,gptChatDTO.getContent(), finalIsNewConversation);
            }
        });

        return sseEmitter;
    }

    /**
     * 获取会话分页
     * @param pageParam 分页参数
     * @return 会话分页
     */
    @Override
    public PageResult<GptConversationPage> pageConversation(PageParam pageParam) {
        //TODO 后期上角色权限
        GPTUser user = AuthUtil.getUser();
        if (user == null) {
            throw new ServiceException(ChatResultCode.NOT_AUTHORIZED);
        }
        GptConversationPageDTO gptConversationPageDTO = new GptConversationPageDTO();
        gptConversationPageDTO.setUserId(user.getUserId());
        gptConversationPageDTO.setPageNo(pageParam.getPageNo());
        gptConversationPageDTO.setPageSize(pageParam.getPageSize());
        gptConversationPageDTO.setPageNo(pageParam.getPageNo());
        return gptConversationService.page(gptConversationPageDTO);
    }

    /**
     * 聊天记录列表
     * @param conversationId 会话id
     * @return 聊天记录列表
     */
    @Override
    public List<GptConversationDetail> listConversationDetail(String conversationId) {
        return gptConversationService.listConversationDetail(conversationId);
    }

    /**
     * 分页查询聊天记录列表
     * @param pageParam 分页参数
     * @param conversationId 会话id
     * @return 聊天记录列表
     */
    @Override
    public PageResult<GptConversationDetail> listConversationDetailByPage(PageParam pageParam, String conversationId) {
        return gptConversationService.listConversationDetailByPage(pageParam,conversationId);
    }


    /**
     * 保存聊天记录
     * @param conversationId 会话id
     * @param gptId Gpt角色id
     * @param gptContext gpt内容
     * @param userId 用户id
     * @param userContext 用户内容
     * @param isNewConversation 是否为新的会话
     */
    public void saveChatHistory(String conversationId,Long gptId,String gptContext,Long userId,String userContext,boolean isNewConversation){
        //1.创建conversation
        if(isNewConversation){
            GptConversation gptConversation = new GptConversation();
            gptConversation.setConversationId(conversationId);
            gptConversation.setTitle(getOverview(gptContext));
            gptConversation.setGptRoleId(gptId);
            gptConversation.setCreateUser(userId);
            gptConversation.setUpdateUser(userId);
            gptConversationService.addConversation(gptConversation);
        }
        //2.保持历史记录
        GptSaveChatMessage gptSaveChatMessage = new GptSaveChatMessage();
        List<GptSaveChatMessage.MessageContext> messageContexts = new ArrayList<>();
        GptSaveChatMessage.MessageContext messageContext = new GptSaveChatMessage.MessageContext();
        messageContext.setType(1);
        messageContext.setMessage(userContext);
        messageContext.setUserId(userId);
        messageContexts.add(messageContext);
        GptSaveChatMessage.MessageContext gptMessage = new GptSaveChatMessage.MessageContext();
        gptMessage.setType(2);
        gptMessage.setMessage(gptContext);
        gptMessage.setUserId(gptId);
        messageContexts.add(gptMessage);
        gptSaveChatMessage.setConversationId(conversationId);
        gptSaveChatMessage.setMessageContext(messageContexts);
        gptConversationService.saveChatHistory(gptSaveChatMessage);
    }

    /**
     * 获取历史记录
     * @param conversationId 会话id
     * @return 历史记录
     */
    private List<ChatMessage> getHistoryMessage(String conversationId,Integer contextLength) {
        List<GptConversationDetail> gptConversationDetails = gptConversationService.listConversationHistory(conversationId, contextLength);
        if(ObjectUtil.isNotEmpty(gptConversationDetails)){
            return gptConversationDetails.stream().map(item->{
                if(item.getType()==1){
                    return createUserMessage(item.getMessage());
                }else{
                    return createAiMessage(item.getMessage());
                }
                    }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 检测是否为uuid
     * @param uuid uuid
     * @return 是否为uuid
     */
    private Boolean checkIsUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 创建AI消息
     * @param text 文本
     * @return AI消息
     */

    private AiMessage createAiMessage(String text) {
        return new AiMessage(text);
    }

    /**
     * 创建用户消息
     * @param text 文本
     * @return 用户消息
     */
    private UserMessage createUserMessage(String text) {
        return new UserMessage(text);
    }

    /**
     * 获取sseKey
     * @return sseKey
     */
    private String getSSEKey(){
        GPTUser user = AuthUtil.getUser();
        String account = user.getAccount();
        return account+"_"+UUID.fastUUID().toString();
    }

    /**
     * 获取概览
     * @param gptContext gpt内容
     * @return 概览
     */
    private String getOverview(String gptContext) {
        if (gptContext.length() >= 10) {
            return gptContext.substring(0, 10);
        } else {
            return gptContext + "..........".substring(0, 10 - gptContext.length());
        }
    }

}
