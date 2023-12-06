package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.GptTokenGetDTO;
import com.magicrepokit.chat.entity.UserGpt;
import com.magicrepokit.mp.base.BaseService;

public interface IUserGptService extends BaseService<UserGpt> {
    /**
     * 获取用户的gpt token
     * @param userId
     * @return
     */
    GptTokenGetDTO getGptToken(Long userId);

    /**
     * 减少用户的订阅额度
     * @param userId 用户id
     * @param type 类型[1:普通,2:订阅]
     */
    Boolean reduceSubscriptionCreditLimit(String userId,Integer type);
}
