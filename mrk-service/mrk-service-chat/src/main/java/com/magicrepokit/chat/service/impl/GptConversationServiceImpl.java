package com.magicrepokit.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private final static Integer DEFAULT_MAX_COUNT = 20;
    private final static String DEFAULT_MAX_TOKEN = 1000L + "";


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
    public boolean saveConversation(GptSaveChatMessage gptSaveChatMessage) {
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
        return null;
    }

    /**
     * 获取聊天历史记录
     * @param conversationId 会话id
     * @param maxToken 最大token
     * @return 聊天历史记录
     */
    @Override
    public List<GptConversationDetail> listConversationHistory(String conversationId, String maxToken) {

        return null;
    }
}
