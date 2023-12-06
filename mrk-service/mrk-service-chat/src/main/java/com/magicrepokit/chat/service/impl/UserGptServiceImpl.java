package com.magicrepokit.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.constant.StatusConstant;
import com.magicrepokit.chat.dto.GptTokenGetDTO;
import com.magicrepokit.chat.entity.UserGpt;
import com.magicrepokit.chat.mapper.UserGptMapper;
import com.magicrepokit.chat.service.IUserGptService;
import com.magicrepokit.mp.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserGptServiceImpl extends BaseServiceImpl<UserGptMapper, UserGpt> implements IUserGptService {
    /**
     * 获取用户的gpt token
     * @param userId 用户id
     * @return gpt token
     */
    @Override
    public GptTokenGetDTO getGptToken(Long userId) {
        GptTokenGetDTO result = new GptTokenGetDTO();
        //获取用户自己的
        UserGpt userGpt = this.getOne(new LambdaQueryWrapper<UserGpt>()
                .eq(UserGpt::getUserId, userId)
        );
        //用户无管理
        if(userGpt==null){
            result.setStatus(StatusConstant.GPT_NO_ACCOUNT);
            return result;
        }

        //初始化
        result.setStatus(StatusConstant.GPT_NORMAL);
        result.setSubscriptionCreditLimit(userGpt.getSubscriptionCreditLimit());
        result.setRegularCreditLimit(userGpt.getRegularCreditLimit());

        //判断用户是否有token
        if(userGpt.getOpenToken()!=null){
            result.setToken(userGpt.getOpenToken());
            result.setStatus(StatusConstant.GPT_SELF_ACCOUNT);
            return result;
        }
        //判断用户是否有额度
        if(userGpt.getRegularCreditLimit()==null||userGpt.getRegularCreditLimit()<=0){
            result.setStatus(StatusConstant.GPT_NO_REGULAR_CREDIT_LIMIT);
            return result;
        }
        //判断用户是否有订阅额度
        if(userGpt.getSubscriptionCreditLimit()==null||userGpt.getSubscriptionCreditLimit()<=0){
            result.setToken(this.getSystemGpt().getOpenToken());
            result.setStatus(StatusConstant.GPT_NO_SUBSCRIPTION_CREDIT_LIMIT);
            return result;
        }
        return result;

    }

    /**
     * 减少用户的订阅额度
     * @param userId 用户id
     * @param type 类型[1:普通,2:订阅]
     */
    @Override
    public Boolean reduceSubscriptionCreditLimit(String userId,Integer type) {
        //获取用户自己的
        UserGpt userGpt = this.getOne(new LambdaQueryWrapper<UserGpt>()
                .eq(UserGpt::getUserId, userId)
        );
        if(userGpt==null){
            return false;
        }
        //type=1,减少普通额度
        if(type==1){
            userGpt.setRegularCreditLimit(userGpt.getRegularCreditLimit()-1);
            if(userGpt.getRegularCreditLimit()<0){
                return false;
            }
            //乐观锁
            return this.update(userGpt,new LambdaQueryWrapper<UserGpt>()
                    .eq(UserGpt::getUserId, userId)
                    .eq(UserGpt::getRegularCreditLimit, userGpt.getRegularCreditLimit())
            );
        }
        //type=2,减少订阅额度
        if(type==2){
            userGpt.setSubscriptionCreditLimit(userGpt.getSubscriptionCreditLimit()-1);
            if(userGpt.getSubscriptionCreditLimit()<0){
                return false;
            }
            //乐观锁
             return this.update(userGpt, new LambdaQueryWrapper<UserGpt>()
                    .eq(UserGpt::getUserId, userId)
                    .eq(UserGpt::getSubscriptionCreditLimit, userGpt.getSubscriptionCreditLimit())
            );
        }
        return true;
    }

    /**
     * 获取系统的gpt
     * @return
     */
    private UserGpt getSystemGpt() {
        //获取系统的
        return this.getById(0L);
    }
}
