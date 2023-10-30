package com.magicrepokit.system.service.impl;

import cn.hutool.json.JSONUtil;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mp.base.BaseServiceImpl;
import com.magicrepokit.system.constant.SocialTypeEnum;
import com.magicrepokit.social.factory.MRKAuthRequestFactory;
import com.magicrepokit.system.constant.SystemResultCode;
import com.magicrepokit.system.entity.SocialUser;
import com.magicrepokit.system.entity.SocialUserBind;
import com.magicrepokit.system.entity.vo.SocialUserAuthVO;
import com.magicrepokit.system.mapper.SocialUserBindMapper;
import com.magicrepokit.system.mapper.SocialUserMapper;
import com.magicrepokit.system.service.ISocialUserService;
import com.xingyuv.jushauth.model.AuthCallback;
import com.xingyuv.jushauth.model.AuthResponse;
import com.xingyuv.jushauth.model.AuthUser;
import com.xingyuv.jushauth.request.AuthRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class SocialUserServiceImpl extends BaseServiceImpl<SocialUserMapper, SocialUser> implements ISocialUserService {
    @Autowired
    private SocialUserMapper socialUserMapper;
    @Autowired
    private MRKAuthRequestFactory mrkAuthRequestFactory;
    @Autowired
    private SocialUserBindMapper socialUserBindMapper;

    /**
     * 获取社交账户信息
     *
     * @param type 账户类型
     * @param code 授权码
     * @param state 状态码
     * @return 社交账户信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SocialUserAuthVO authSocialUser(Integer type, String code, String state) {
        //获取社交用户
        SocialUser socialUserVO = getSocialUser(type, code, state);
        //获取绑定账户
        SocialUserBind socialUserBind = socialUserBindMapper.selectBySocialUserId(socialUserVO.getId());
        if(socialUserBind==null){
            throw new ServiceException(SystemResultCode.AUTH_THIRD_LOGIN_NOT_BIND);
        }
        return new SocialUserAuthVO(socialUserVO.getOpenid(),socialUserBind.getUserId());
    }

    @Transactional
    public SocialUser getSocialUser(Integer type, String code, String state) {
        //1.从数据库中获取
        SocialUser socialUser = socialUserMapper.selectByTypeAndCodeAndCode(type, code, state);
        if(socialUser==null){
            //2.三方平台获取
            AuthUser authUser = authUser(type, code, state);
            socialUser = socialUserMapper.selectByOpenid(authUser.getUuid());
            if(socialUser==null){
                socialUser = new SocialUser();
            }
            socialUser.setType(type).setCode(code).setState(state) // 需要保存 code + state 字段，保证后续可查询
                    .setOpenid(authUser.getUuid()).setToken(authUser.getToken().getAccessToken())
                    .setNickName(authUser.getNickname()).setAvatar(authUser.getAvatar());
            if(socialUser.getId()==null){
                socialUserMapper.insert(socialUser);
            }else{
                socialUserMapper.updateById(socialUser);
            }
        }
        return socialUser;
    }

    /**
     * 三方获取用户信息
     *
     * @param type 账户类型
     * @param code 授权码
     * @param state 状态码
     * @return 社交账户信息
     */
    private AuthUser authUser(Integer type, String code, String state) {
        SocialTypeEnum socialTypeEnum = SocialTypeEnum.valueOfType(type);
        if(socialTypeEnum==null){
            throw new ServiceException(SystemResultCode.NOT_FOUND_SOCIAL_TYPE);
        }
        AuthRequest authRequest = mrkAuthRequestFactory.get(socialTypeEnum.getSource());
        AuthCallback authCallback = AuthCallback.builder().code(code).state(state).build();
        AuthResponse<?> authResponse = authRequest.login(authCallback);
        log.info("[getAuthUser][请求社交平台 type({}) request({}) response({})]", type,
                JSONUtil.toJsonStr(authCallback), JSONUtil.toJsonStr(authResponse));
        if(!authResponse.ok()){
            throw new ServiceException(SystemResultCode.SOCIAL_USER_AUTH_FAILURE,authResponse.getMsg());
        }
        return (AuthUser) authResponse.getData();
    }
}
