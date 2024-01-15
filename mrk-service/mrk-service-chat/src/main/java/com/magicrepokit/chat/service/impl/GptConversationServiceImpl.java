package com.magicrepokit.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.component.LangchainComponent;
import com.magicrepokit.chat.constant.GptModel;
import com.magicrepokit.chat.converter.GptConverter;
import com.magicrepokit.chat.dto.gpt.GptConversationPageDTO;
import com.magicrepokit.chat.dto.gpt.GptSaveChatMessage;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.entity.GptRole;
import com.magicrepokit.chat.mapper.GptConversationMapper;
import com.magicrepokit.chat.service.IGptConversationDetailService;
import com.magicrepokit.chat.service.IGptConversationService;
import com.magicrepokit.chat.service.IGptRoleService;
import com.magicrepokit.chat.vo.gpt.GptConversationPage;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.utils.ObjectUtil;
import com.magicrepokit.mb.base.BaseServiceImpl;
import com.magicrepokit.mb.base.PageParam;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.Tokenizer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GptConversationServiceImpl extends BaseServiceImpl<GptConversationMapper, GptConversation> implements IGptConversationService {
    private final IGptConversationDetailService gptConversationDetailService;
    private final GptConverter gptConverter;
    private final IGptRoleService gptRoleService;
    private final LangchainComponent langchainComponent;
    private final static Integer DEFAULT_MAX_COUNT = 20;
    private final static Integer DEFAULT_MAX_TOKEN = 1500;

    private final static Integer DEFAULT_WORDS = 25;


    @Override
    public PageResult<GptConversationPage> page(GptConversationPageDTO gptConversationPageDTO) {
        LambdaQueryWrapper<GptConversation> query = new LambdaQueryWrapper<GptConversation>()
                .eq(GptConversation::getCreateUser, gptConversationPageDTO.getUserId())
                .orderByDesc(GptConversation::getCreateTime);
        PageResult<GptConversation> result = selectPage(gptConversationPageDTO, query);
        List<GptConversation> list = result.getList();
        if (ObjectUtil.isEmpty(list)) {
            return new PageResult<>();
        }
        List<Long> gptRoleIds = list.stream().map(GptConversation::getGptRoleId).collect(Collectors.toList());
        Map<Long, GptRole> gptRoleMap = gptRoleService.queryIdMap(gptRoleIds);
        return result.convert(item -> {
            return gptConverter.entity2Page(item, ObjectUtil.isEmpty(gptRoleMap) ? null : gptRoleMap.get(item.getGptRoleId()));
        });
    }

    @Override
    public List<GptConversationDetail> listConversationDetail(String conversationId) {
        return gptConversationDetailService.list(new LambdaQueryWrapper<GptConversationDetail>().eq(GptConversationDetail::getConversationId, conversationId)
                .orderByDesc(GptConversationDetail::getCreateTime));
    }

    @Override
    public PageResult<GptConversationDetail> listConversationDetailByPage(PageParam pageParam, String conversationId) {
        return gptConversationDetailService.selectPage(pageParam, new LambdaQueryWrapper<GptConversationDetail>().eq(GptConversationDetail::getConversationId, conversationId)
                .orderByDesc(GptConversationDetail::getCreateTime));
    }

    @Override
    public boolean saveChatHistory(GptSaveChatMessage gptSaveChatMessage) {
        String conversationId = gptSaveChatMessage.getConversationId();
        List<GptSaveChatMessage.MessageContext> messageContext = gptSaveChatMessage.getMessageContext();
        if (ObjectUtil.isEmpty(messageContext)) {
            return true;
        }
        List<GptConversationDetail> gptConversationDetails = messageContext.stream().map(item -> {
            GptConversationDetail gptConversationDetail = new GptConversationDetail();
            gptConversationDetail.setConversationId(conversationId);
            gptConversationDetail.setUserId(item.getUserId());
            gptConversationDetail.setType(item.getType());
            gptConversationDetail.setMessage(item.getMessage());
            return gptConversationDetail;
        }).collect(Collectors.toList());
        return gptConversationDetailService.saveBatch(gptConversationDetails);
    }

    @Override
    public boolean addConversation(GptConversation gptConversation) {
        return save(gptConversation);
    }

    /**
     * 获取聊天历史记录
     * @param conversationId 会话id
     * @param maxCount 最大条数
     * @return 聊天历史记录
     */
    @Override
    public List<GptConversationDetail> listConversationHistory(String conversationId, Integer maxCount) {
        if(ObjectUtil.isEmpty(maxCount)){
            maxCount = DEFAULT_MAX_COUNT;
        }
        //1.查询倒数maxCount条记录
        return gptConversationDetailService.list(new LambdaQueryWrapper<GptConversationDetail>().eq(GptConversationDetail::getConversationId, conversationId)
                .orderByDesc(GptConversationDetail::getCreateTime).last("limit " + maxCount));
    }

    /**
     * 获取聊天历史记录
     * @param conversationId 会话id
     * @param maxToken 最大token
     * @param gptModel gpt模型
     * @return 聊天历史记录
     */
    @Override
    public List<GptConversationDetail> listConversationHistory(String conversationId, Integer maxToken, GptModel gptModel) {
        if(ObjectUtil.isEmpty(maxToken)){
            maxToken = DEFAULT_MAX_TOKEN;
        }
        //1.默认查询数量
        List<GptConversationDetail> list = gptConversationDetailService.list(new LambdaQueryWrapper<GptConversationDetail>().eq(GptConversationDetail::getConversationId, conversationId)
                .orderByDesc(GptConversationDetail::getCreateTime).last("limit " + getSelectCount(maxToken)));
        if(ObjectUtil.isEmpty(list)){
            return null;
        }
        List<ChatMessage> chatMessages =  gptConverter.conversationDetail2ChatMessage(list);
        //计算token
        Tokenizer openAiTokenizer = langchainComponent.getOpenAiTokenizer(gptModel);
        int token = openAiTokenizer.estimateTokenCountInMessages(chatMessages);
        //2.如果token小于maxToken，直接返回
        if(token<=maxToken){
            return list;
        }else{
            while (token>maxToken&&list.size()>1){
                list.remove(0);
                chatMessages =  gptConverter.conversationDetail2ChatMessage(list);
                token = openAiTokenizer.estimateTokenCountInMessages(chatMessages);
            }
        }
        return list;
    }

    private int getSelectCount(Integer maxToken){
        return (maxToken / DEFAULT_WORDS)+1;
    }
}
