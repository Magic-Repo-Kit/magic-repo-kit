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
import com.magicrepokit.chat.vo.gpt.GptConversationPage;
import com.magicrepokit.chat.vo.gptRole.GptRoleVO;
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

import static java.util.stream.Collectors.joining;

@Service
@AllArgsConstructor
public class GptServiceImpl implements IGptService {
    private final SseEmitterComponent sseEmitterComponent;
    private final IGptConversationService gptConversationService;
    private final IGptRoleService gptRoleService;
    private final LangchainComponent langchainComponent;
    private List <ChatMessage> chatMessages;
    @Override
    public SseEmitter chatRole(GptChatDTO gptChatDTO) {
        //获取连接
        MRKUser user = AuthUtil.getUser();
        SseEmitter sseEmitter = sseEmitterComponent.SseEmitterConnect(user.getAccount());
        //查询模型信息
        GptRoleVO gptRoleVO = getGptRole(gptChatDTO.getRoleId());
        //建立模型
        StreamingChatLanguageModel streamingChatLanguageModel = langchainComponent.getStreamingChatLanguageModel(getGptModel(gptRoleVO.getModelName()));
        List<ChatMessage> chatMessages = new ArrayList<>();

        //1.系统提示词
        SystemMessage systemMessage = new SystemMessage(gptRoleVO.getPrompt());
        chatMessages.add(systemMessage);

        //2.知识库内容
        if(ObjectUtil.isNotEmpty(gptRoleVO.getKnowledgeFileListVO())){
            SystemMessage knowledgeMessage = getKnowledgeMessage(gptChatDTO.getContent(), gptRoleVO.getKnowledgeFileListVO().getIndexName());
            if(ObjectUtil.isNotEmpty(knowledgeMessage)){
                chatMessages.add(knowledgeMessage);
            }
        }
        //3.历史记忆
        if(ObjectUtil.isNotEmpty(gptChatDTO.getIsContext())&&gptChatDTO.getIsContext().equals(StatusConstant.YES)){
            SystemMessage contextMessage = getHistoryMessage(gptChatDTO.getConversationId());
            if(ObjectUtil.isNotEmpty(contextMessage)){
                chatMessages.add(contextMessage);
            }
        }
        //4.TODO 是否使用联网



        //5.用户问题
        UserMessage userMessage = new UserMessage(gptChatDTO.getContent());
        chatMessages.add(userMessage);
        //6.聊天
        streamingChatLanguageModel.generate(chatMessages, new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                sseEmitterComponent.SseEmitterSendMessage(token, user.getAccount());
            }

            @Override
            public void onError(Throwable error) {
                throw new ServiceException(ChatResultCode.CHAT_ERROR+" "+error.getMessage());
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                sseEmitterComponent.SseEmitterSendComplateMessage(response.toString(), user.getAccount());
                sseEmitterComponent.close(user.getAccount());
                //保存聊天记录
                saveChatHistory(gptChatDTO.getConversationId(),gptChatDTO.getRoleId(),response.content().text(),gptChatDTO.getContent());
                StreamingResponseHandler.super.onComplete(response);
            }
        });

        return sseEmitter;
    }

    private void saveChatHistory(String conversationId,Long gptId,String gptContext,String userContext){
        //1.创建conversation
        if(ObjectUtil.isEmpty(conversationId)){
            conversationId = UUID.randomUUID().toString();
            GptConversation gptConversation = new GptConversation();
            gptConversation.setConversationId(conversationId);
            gptConversation.setTitle(getOverview(gptContext));
            gptConversation.setGptRoleId(gptId);
            gptConversationService.addConversation(gptConversation);
        }
        //2.保持历史记录
        GptSaveChatMessage gptSaveChatMessage = new GptSaveChatMessage();
        List<GptSaveChatMessage.MessageContext> messageContexts = new ArrayList<>(2);
        messageContexts.add(new GptSaveChatMessage.MessageContext(1,userContext,AuthUtil.getUser().getUserId()));
        messageContexts.add(new GptSaveChatMessage.MessageContext(2,gptContext,gptId));
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
    private SystemMessage getHistoryMessage(String conversationId) {
        //知识库内容
        PromptTemplate promptTemplate = new PromptTemplate("以下是历史记录:\n" +
                "{{history}}"
        );
        List<GptConversationDetail> gptConversationDetails = gptConversationService.listConversationHistory(conversationId, null);
        if(ObjectUtil.isNotEmpty(gptConversationDetails)){
            String context = gptConversationDetails.stream().map(item->{
                String prefix = (item.getType() == 1) ? "user: " : "ai: ";
                return prefix + item.getMessage();
                    }).collect(joining("\n\n"));
            Map<String,Object> map = new HashMap<>();
            map.put("history",context);
            Prompt prompt = promptTemplate.apply(map);
            return new SystemMessage(prompt.text());
        }
        return null;
    }

    /**
     * 获取知识库内容
     * @param context 问题
     * @param indexName 索引名称
     * @return 知识库内容
     */
    private SystemMessage getKnowledgeMessage(String context, String indexName) {
        //知识库内容
        PromptTemplate promptTemplate = new PromptTemplate("你可以根据知识库内容回答用户相关问题:\n" +
                "知识库：\n"+
                "{{knowledge}} \n"
        );
        //3.检索知识库
        List<TextSegment> relevant = langchainComponent.findRelevant(indexName, context);
        if(ObjectUtil.isEmpty(relevant)){
            return null;
        }
        String relevantContext = relevant.stream().map(TextSegment::text).collect(joining("\n\n"));
        Map<String,Object> map = new HashMap<>();
        map.put("knowledge",relevantContext);
        Prompt prompt = promptTemplate.apply(map);
        return new SystemMessage(prompt.text());
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

    private UserMessage createUserMessage(String text) {
        return new UserMessage(text);
    }

}
