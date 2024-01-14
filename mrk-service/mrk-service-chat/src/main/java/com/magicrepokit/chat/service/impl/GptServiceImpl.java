package com.magicrepokit.chat.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.magicrepokit.chat.component.LangchainComponent;
import com.magicrepokit.chat.component.SseEmitterComponent;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.constant.StatusConstant;
import com.magicrepokit.chat.dto.gpt.GptChatDTO;
import com.magicrepokit.chat.dto.gpt.GptConversationPageDTO;
import com.magicrepokit.chat.dto.gpt.GptTokenGetDTO;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.service.*;
import com.magicrepokit.chat.vo.gptRole.GptRoleVO;
import com.magicrepokit.chat.vo.knowledge.KnowledgeFileListVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.utils.AuthUtil;
import com.magicrepokit.jwt.entity.MRKUser;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mb.base.PageParam;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
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

import static com.alibaba.nacos.shaded.org.checkerframework.checker.units.UnitsTools.g;
import static java.util.stream.Collectors.joining;

@Service
@AllArgsConstructor
public class GptServiceImpl implements IGptService {
    private final SseEmitterComponent sseEmitterComponent;
    private final IConversationService conversationService;
    private final IUserGptService userGptService;
    private final IGptConversationService gptConversationService;
    private final IGptRoleService gptRoleService;
    private final LangchainComponent langchainComponent;

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
        //获取token
        String token = gptToken.getToken();
        //校验数据类型
        validateGptChatDTO(gptChatDTO);
        //获取连接
        SseEmitter sseEmitter = sseEmitterComponent.SseEmitterConnect(user.getAccount());
        //推送消息
        conversationService.sendMsg(token, gptChatDTO.getMessageId(), gptChatDTO.getContent(), gptChatDTO.getConversationId(), gptChatDTO.getParentMessageId(),
                createConsumer(user.getUserId(), user.getAccount(), gptChatDTO.getMessageId(), gptChatDTO.getParentMessageId(), gptChatDTO.getConversationId(), gptChatDTO.getContent())
        );
        sseEmitterComponent.close(user.getAccount());
        return sseEmitter;
    }

    @Override
    public SseEmitter chatRole(GptChatDTO gptChatDTO) {
        //获取用户信息
        MRKUser user = AuthUtil.getUser();
        //获取连接
        SseEmitter sseEmitter = sseEmitterComponent.SseEmitterConnect(user.getAccount());

        GptRoleVO gptRoleVO = gptRoleService.detailById(gptChatDTO.getRoleId());
        if (ObjectUtil.isEmpty(gptRoleVO)) {
            throw new ServiceException(ChatResultCode.GPT_ROLE_NOT_EXIST);
        }
        //1.建立模型
        StreamingChatLanguageModel streamingChatLanguageModel = streamingChatLanguageModel();
        List<ChatMessage> chatMessages = new ArrayList<>();
        //1.系统提示词
        SystemMessage systemMessage = new SystemMessage(gptRoleVO.getPrompt());
        chatMessages.add(systemMessage);
        //2.用户问题
        PromptTemplate promptTemplate = new PromptTemplate("你可以根据知识库内容回答用户相关问题\n" +
                "知识库：\n"+
                "{{knowledge}} \n"
        );
        KnowledgeFileListVO knowledge = gptRoleVO.getKnowledgeFileListVO();
        //3.检索知识库
        List<TextSegment> relevant = langchainComponent.findRelevant(knowledge.getIndexName(), gptChatDTO.getContent());
        String relevantContext = relevant.stream().map(TextSegment::text).collect(joining("\n\n"));
        Map<String,Object> map = new HashMap<>();
        map.put("knowledge",relevantContext);
        Prompt prompt = promptTemplate.apply(map);
        SystemMessage systemMessage1 = new SystemMessage(prompt.text());
        chatMessages.add(systemMessage1);
        //4.用户问题
        UserMessage userMessage = new UserMessage(gptChatDTO.getContent());
        chatMessages.add(userMessage);
        //5.聊天
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
                StreamingResponseHandler.super.onComplete(response);

            }
        });

        return sseEmitter;
    }

    private ChatLanguageModel chatLanguageModel(){
        return OpenAiChatModel.builder().apiKey("sk-gRbZ9FJz2E7c7mwO5JOvp2u2rtoWoAbg12CxDy3Y25eLeDvd").baseUrl("https://api.chatanywhere.tech/").build();
    }

    private StreamingChatLanguageModel streamingChatLanguageModel(){
        return OpenAiStreamingChatModel.builder().apiKey("sk-gRbZ9FJz2E7c7mwO5JOvp2u2rtoWoAbg12CxDy3Y25eLeDvd").baseUrl("https://api.chatanywhere.tech/").build();
    }

    @Override
    public PageResult<GptConversation> listConversationByPage(PageParam pageParam) {
        //获取用户信息
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
        return null;
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
        if (StrUtil.isBlank(gptChatDTO.getMessageId())) {
            throw new ServiceException(ChatResultCode.MESSAGE_ID_NULL);
        }
        if (!checkIsUUID(gptChatDTO.getMessageId())) {
            throw new ServiceException(ChatResultCode.CHAT_UUID_ERROR);
        }
        //如果父消息uuid不为空检测是否为uuid类型
        if (StrUtil.isBlank(gptChatDTO.getParentMessageId())) {
            throw new ServiceException(ChatResultCode.PARENT_MESSAGE_ID_NULL);
        }
        if (!checkIsUUID(gptChatDTO.getParentMessageId())) {
            throw new ServiceException(ChatResultCode.CHAT_UUID_ERROR);
        }
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
                                if (!gptConversationService.addConversation(userId, conversationId, ask.substring(0, Math.min(ask.length(), 5)) + "...")) {
                                    throw new ServiceException(ChatResultCode.CHAT_ADD_CONVERSATION_ERROR);
                                }
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
                                    if (!gptConversationService.addMessage(conversationId, messageId, parentMessageId, ask, UnicodeUtil.toString(string))) {
                                        throw new ServiceException(ChatResultCode.CHAT_ADD_CONVERSATION_ERROR);
                                    }
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

}
