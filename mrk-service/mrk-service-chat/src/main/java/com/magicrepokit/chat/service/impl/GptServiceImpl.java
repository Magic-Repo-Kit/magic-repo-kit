package com.magicrepokit.chat.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.magicrepokit.chat.component.LangchainComponent;
import com.magicrepokit.chat.component.SseEmitterComponent;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.constant.GptModel;
import com.magicrepokit.chat.constant.StatusConstant;
import com.magicrepokit.chat.dto.gpt.GptChatDTO;
import com.magicrepokit.chat.dto.gpt.GptConversationPageDTO;
import com.magicrepokit.chat.dto.gpt.GptSaveChatMessage;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.service.*;
import com.magicrepokit.chat.vo.Error;
import com.magicrepokit.chat.vo.gpt.GptConversationPage;
import com.magicrepokit.chat.vo.gpt.GptSSEResponse;
import com.magicrepokit.chat.vo.gpt.KnowledgeText;
import com.magicrepokit.chat.vo.gptRole.GptRoleVO;
import com.magicrepokit.chat.vo.knowledge.KnowledgeFileListVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.utils.AuthUtil;
import com.magicrepokit.jwt.entity.MRKUser;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mb.base.PageParam;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Service
@AllArgsConstructor
@Slf4j
public class GptServiceImpl implements IGptService {
    private final SseEmitterComponent sseEmitterComponent;
    private final IGptConversationService gptConversationService;
    private final IGptRoleService gptRoleService;
    private final LangchainComponent langchainComponent;
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
        MRKUser user = AuthUtil.getUser();
        Long userId = user.getUserId();
        String account = user.getAccount();
        SseEmitter sseEmitter = sseEmitterComponent.SseEmitterConnect(account);
        //查询模型信息
        GptRoleVO gptRoleVO = getGptRole(gptChatDTO.getRoleId());
        //建立模型
        StreamingChatLanguageModel streamingChatLanguageModel = langchainComponent.getStreamingChatLanguageModel(getGptModel(gptRoleVO.getModelName()),new Double(gptRoleVO.getTemperature()));
        List<ChatMessage> chatMessages = new ArrayList<>();

        //1.系统提示词
        SystemMessage systemMessage = new SystemMessage(gptRoleVO.getPrompt());
        //2.知识库内容
        List<TextSegment> relevant = null;
        if(ObjectUtil.isNotEmpty(gptRoleVO.getKnowledgeFileListVO())){
            KnowledgeFileListVO knowledge = gptRoleVO.getKnowledgeFileListVO();
            relevant = getRelevant(knowledge.getIndexName(),gptChatDTO.getContent(),knowledge.getMinScore(),knowledge.getMaxResult());
            SystemMessage knowledgeMessage = getKnowledgeMessage(relevant);
            String text = systemMessage.text();
            if(ObjectUtil.isNotEmpty(knowledgeMessage)){
                text = text+"\n\n"+knowledgeMessage.text();
                systemMessage = createSystemMessage(text);
            }
        }
        chatMessages.add(systemMessage);


        //3.历史记忆
        if(ObjectUtil.isNotEmpty(gptChatDTO.getIsContext())&&gptChatDTO.getIsContext().equals(StatusConstant.YES)){
            List<ChatMessage> historyMessage = getHistoryMessage(gptChatDTO.getConversationId(),gptChatDTO.getContextLength());
            if(ObjectUtil.isNotEmpty(historyMessage)){
                chatMessages.addAll(historyMessage);
            }
        }
        //4.TODO 是否使用联网
        //5.TODO 对话内容上下文自动补全



        //5.用户问题
        UserMessage userMessage = new UserMessage(gptChatDTO.getContent());
        chatMessages.add(userMessage);
        //6.聊天
        List<TextSegment> finalRelevant = relevant;
        boolean finalIsNewConversation = isNewConversation;
        streamingChatLanguageModel.generate(chatMessages, new StreamingResponseHandler<AiMessage>() {
            @Override
            @Async
            public void onNext(String token) {
                gptSSEResponse.setMessage(token);
                gptSSEResponse.setError(null);
                gptSSEResponse.setIsEnd(false);
                sseEmitterComponent.SseEmitterSendMessage(gptSSEResponse, user.getAccount());
            }

            @Override
            @Async
            public void onError(Throwable error) {
                Error error1 = new Error();
                error1.setCode(ChatResultCode.CHAT_ERROR.getCode()+"");
                error1.setMessage(ChatResultCode.CHAT_ERROR.getMessage());
                gptSSEResponse.setError(error1);
                gptSSEResponse.setIsEnd(true);
                sseEmitterComponent.SseEmitterSendMessage(gptSSEResponse, user.getAccount());
                sseEmitterComponent.close(user.getAccount());
                log.error("聊天异常:",error);
            }

            @Override
            @Async
            public void onComplete(Response<AiMessage> response) {
                StreamingResponseHandler.super.onComplete(response);
                gptSSEResponse.setError(null);
                gptSSEResponse.setMessage(response.content().text());
                if(ObjectUtil.isNotEmpty(finalRelevant)&&gptRoleVO.getIsShowKnowledge().equals(StatusConstant.YES)){
                    gptSSEResponse.ofKnowledgeText(finalRelevant);
                }
                gptSSEResponse.setIsEnd(true);
                sseEmitterComponent.SseEmitterSendMessage(gptSSEResponse, user.getAccount());
                sseEmitterComponent.close(user.getAccount());
                //保存聊天记录
                saveChatHistory(gptChatDTO.getConversationId(),gptChatDTO.getRoleId(),response.content().text(),userId,gptChatDTO.getContent(), finalIsNewConversation);
            }
        });

        return sseEmitter;
    }


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

    private String getOverview(String gptContext) {
        if (gptContext.length() >= 10) {
            return gptContext.substring(0, 10);
        } else {
            return gptContext + "..........".substring(0, 10 - gptContext.length());
        }
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
     * 获取知识库内容
     * @param relevant 相关内容
     * @return 知识库内容
     */
    private SystemMessage getKnowledgeMessage(List<TextSegment> relevant) {
        //知识库内容
        PromptTemplate promptTemplate = new PromptTemplate("以下是知识库的内容：\n" +
                "知识库:\n"+
                "{{knowledge}}"
        );
        String relevantContext = ObjectUtil.isEmpty(relevant)?"空":relevant.stream().map(TextSegment::text).collect(joining("\n\n"));
        Map<String,Object> map = new HashMap<>();
        map.put("knowledge",relevantContext);
        Prompt prompt = promptTemplate.apply(map);
        return new SystemMessage(prompt.text());
    }

    /**
     * 获取相关内容
     * @param indexName
     * @param context
     * @return
     */
    private List<TextSegment> getRelevant(String indexName, String context,Double minScore,Integer maxResult) {
        return langchainComponent.findRelevant(indexName, context, maxResult, minScore);
    }

    /**
     * 获取Gpt角色信息
     * @param roleId Gpt角色id
     * @return Gpt角色信息
     */
    private GptRoleVO getGptRole(Long  roleId) {
        GptRoleVO gptRoleVO = gptRoleService.detailById(roleId);
        if (ObjectUtil.isEmpty(gptRoleVO)) {
            throw new ServiceException(ChatResultCode.GPT_ROLE_NOT_EXIST);
        }
        return gptRoleVO;
    }

    /**
     * 获取模型信息
     * @param name 模型名称
     * @return 模型信息
     */
    private GptModel getGptModel(String  name) {
        GptModel gptModel = GptModel.getByModelName(name);
        if (ObjectUtil.isEmpty(gptModel)) {
            throw new ServiceException(ChatResultCode.GPT_MODEL_NAME_ERROR);
        }
        return gptModel;
    }


    @Override
    public PageResult<GptConversationPage> pageConversation(PageParam pageParam) {
        //TODO 后期上角色权限
        MRKUser user = AuthUtil.getUser();
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

    @Override
    public List<GptConversationDetail> listConversationDetail(String conversationId) {
        return gptConversationService.listConversationDetail(conversationId);
    }

    @Override
    public PageResult<GptConversationDetail> listConversationDetailByPage(PageParam pageParam, String conversationId) {
        return gptConversationService.listConversationDetailByPage(pageParam,conversationId);
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
     * 流处理
     *
     * @param account         用户账户
     * @param userId          用户id
     * @param messageId       消息id
     * @param parentMessageId 父消息id
     * @param conversationId  会话id
     * @param ask             问题
     * @return 流处理
     */
    private Consumer<InputStream> createConsumer(Long userId, String account, String messageId, String parentMessageId, String conversationId, String ask) {
        return new Consumer<InputStream>() {
            @Override
            public void accept(InputStream inputStream) {
                SSEClientSend(inputStream, userId, account, messageId, parentMessageId, conversationId, ask);
            }
        };
    }

    /**
     * 解析流
     *
     * @param inputStream     输入流
     * @param userId          用户id
     * @param account         用户账户
     * @param messageId       消息id
     * @param parentMessageId 父消息id
     * @param conversationId  会话id
     * @param ask             问题
     */
    private void SSEClientSend(InputStream inputStream, Long userId, String account, String messageId, String parentMessageId, String conversationId, String ask) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String line;
        //缓存上一条数据
        JSONObject end = null;
        JSONObject lastResponse = null;
        int index = 0;
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
                    if (!line.trim().startsWith("{")||!line.trim().endsWith("}")) {
                        continue;
                    }
                    JSONObject local = JSONUtil.parseObj(line);
                    if (index == 0) {
                        end = local;
                        lastResponse = local;
                    }
                    //if(local.getIsCompletion()!=null&& local.getIsCompletion())
                    if (local.getBool("done") != null && local.getBool("done")) {
                        //存入数据库
                        //1.判断是否为新的会话
                        System.out.println("end:" + end);
                        if (conversationId == null||conversationId.equals("")) {
                            conversationId = end.getStr("conversation_id");
                            if (conversationId != null&&!conversationId.equals("")) {
//                                if (!gptConversationService.addConversation(userId, conversationId, ask.substring(0, Math.min(ask.length(), 5)) + "...")) {
//                                    throw new ServiceException(ChatResultCode.CHAT_ADD_CONVERSATION_ERROR);
//                                }
                            }
                        }
                        JSONObject message = lastResponse.getJSONObject("message");
                        if (message != null) {
                            JSONObject content = message.getJSONObject("content");
                            if (content != null) {
                                JSONArray parts = content.getJSONArray("parts");
                                if (parts != null) {
                                    String string = parts.get(0).toString();
                                    //2.存入消息
//                                    if (!gptConversationService.addMessage(conversationId, messageId, parentMessageId, ask, UnicodeUtil.toString(string))) {
//                                        throw new ServiceException(ChatResultCode.CHAT_ADD_CONVERSATION_ERROR);
//                                    }
                                }
                            }
                        }
                    }


                    lastResponse = end;
                    end = local;

                    sseEmitterComponent.SseEmitterSendMessage(line, account);
                    index++;
                }
            }
        } catch (IOException e) {
            // 处理异常
            throw new ServiceException(ChatResultCode.CHAT_ERROR);
        }
    }

    private SystemMessage createSystemMessage(String text) {
        return new SystemMessage(text);
    }

    private AiMessage createAiMessage(String text) {
        return new AiMessage(text);
    }

    private UserMessage createUserMessage(String text) {
        return new UserMessage(text);
    }

}
