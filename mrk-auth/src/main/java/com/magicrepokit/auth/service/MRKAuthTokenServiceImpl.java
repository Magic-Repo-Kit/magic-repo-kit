package com.magicrepokit.auth.service;



import cn.hutool.core.util.IdUtil;
import com.magicrepokit.auth.constant.MRKAuthConstant;
import com.magicrepokit.auth.constant.MRKI18NEnum;
import com.magicrepokit.auth.entity.AuthAccessToken;
import com.magicrepokit.common.api.ResultCode;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.redis.utils.MRKRedisUtils;
import com.magicrepokit.user.entity.AuthClient;
import com.magicrepokit.user.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 令牌服务
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Service
public class MRKAuthTokenServiceImpl implements MRKAuthTokenService {
    @Autowired
    private MRKRedisUtils mrkRedisUtils;

    @Override
    public AuthAccessToken createAccessToken(UserInfo userInfo, AuthClient client, List<String> scopes,String userType) {
        AuthAccessToken accessToken = new AuthAccessToken();
        //请求token
        accessToken.setAccessToken(generateToken())
        //刷新toke
        .setRefreshToken(generateToken())
        //userId
        .setUserId(userInfo.getUser().getId())
        //userType
        .setUserType(null)
        //clientId
        .setClientId(client.getClientId())
        //scopes
        .setScopes(scopes)
        .setAccessTokenExpireTime(client.getAccessTokenValiditySeconds())
        //ExpiresTime
        .setExpiresTime(LocalDateTime.now().plusSeconds(client.getRefreshTokenValiditySeconds()));
        //存入redis中
        saveToken(userType, accessToken.getAccessToken(),accessToken.getRefreshToken(),client.getAccessTokenValiditySeconds(),accessToken,client.getRefreshTokenValiditySeconds());
        //记录用户登录状态只允许其一
        offlineUser(userInfo.getUser().getId(), userType);
        //重新记录用户状态
        saveUserStatus(accessToken.getAccessToken(),accessToken.getRefreshToken(),userType,userInfo.getUser().getId(),client.getRefreshTokenValiditySeconds());
        return accessToken;
    }


    @Override
    public AuthAccessToken refreshAccessToken(String refreshToken,String userType) {
        if (!mrkRedisUtils.hasKey(getRedisRefreshTokenKey(userType,refreshToken))) {
            throw new ServiceException(ResultCode.UN_AUTHORIZED, MRKI18NEnum.INVALID_TOKEN.getMessage());
        }
        AuthAccessToken accessToken = (AuthAccessToken) mrkRedisUtils.get(getRedisRefreshTokenKey(userType, refreshToken));
        //下线当前状态
        offlineUser(accessToken.getUserId(), userType);
        //更换token
        accessToken.setAccessToken(generateToken()).setRefreshToken(generateToken()).setExpiresTime(LocalDateTime.now().plusSeconds(accessToken.getAccessTokenExpireTime()));
        //重新记录token
        saveUserStatus(accessToken.getAccessToken(),accessToken.getRefreshToken(),userType,accessToken.getUserId(),accessToken.getRefreshTokenExpireTime());
        saveToken(userType,accessToken.getAccessToken(),accessToken.getRefreshToken(),accessToken.getAccessTokenExpireTime(),accessToken,accessToken.getRefreshTokenExpireTime());

        return accessToken;
    }

    @Override
    public AuthAccessToken removeAccessToken(String accessToken,String userType) {
        if (!mrkRedisUtils.hasKey(getRedisAccessTokenKey(userType,accessToken))) {
            AuthAccessToken accessTokenRedis = (AuthAccessToken) mrkRedisUtils.get(getRedisAccessTokenKey(userType, accessToken));
            //下线当前状态
            offlineUser(accessTokenRedis.getUserId(), userType);
        }

        return null;
    }

    private static String generateToken()  {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 强制用户下线
     * @param userId
     * @param userType
     */
    public void offlineUser(Long userId, String userType) {
        if (mrkRedisUtils.hasKey(getRedisUserStatusKey(userType, userId))) {
            String accessAndRefreshToken = (String) mrkRedisUtils.get(getRedisUserStatusKey(userType, userId));
            String accessTokenRedis = accessAndRefreshToken.split(":")[0];
            String refreshTokenRedis = accessAndRefreshToken.split(":")[1];
            removeToken(accessTokenRedis,refreshTokenRedis, userType);
            removeUserStatus(userType,userId);
        }
    }


    /**
     * 记录token
     * @param userType
     * @param accessTokenStr
     * @param refreshTokenStr
     * @param accessTokenExpireTime
     * @param accessToken
     * @param refreshTokenExpireTime
     */
    private void saveToken(String userType, String accessTokenStr,String refreshTokenStr,Integer accessTokenExpireTime,AuthAccessToken accessToken,Integer refreshTokenExpireTime) {
        //accessToken
        mrkRedisUtils.set(getRedisAccessTokenKey(userType, accessTokenStr), accessToken, accessTokenExpireTime);
        //refreshToken
        mrkRedisUtils.set(getRedisRefreshTokenKey(userType, refreshTokenStr), accessToken, refreshTokenExpireTime);
    }

    /**
     * 移除token
     * 
     * @param accessToken
     * @param refreshToken
     * @param userType
     */
    private void removeToken(String accessToken,String refreshToken,String userType){
        //移除accessToken
        mrkRedisUtils.del(getRedisAccessTokenKey(userType, accessToken));
        //移除refreshToken
        mrkRedisUtils.del(getRedisRefreshTokenKey(userType, refreshToken));
    }

    /**
     * 记录用户状态
     * @param accessToken
     * @param refreshToken
     * @param userType
     * @param userId
     * @param expireTime
     */
    private void saveUserStatus(String accessToken,String refreshToken,String userType,Long userId,int expireTime){
        String value = accessToken+":"+refreshToken;
        mrkRedisUtils.set(getRedisUserStatusKey(userType,userId),value,expireTime);
    }

    private void removeUserStatus(String userType, Long id) {
        mrkRedisUtils.del(getRedisUserStatusKey(userType,id));
    }
    
    private static String getRedisAccessTokenKey(String userType,String token){
        return MRKAuthConstant.REDIS_ACCESS_TOKEN_KEY+userType+":"+token;
    }

    private static String getRedisRefreshTokenKey(String userType,String token){
        return MRKAuthConstant.REDIS_REFRESH_TOKEN_KEY+userType+":"+token;
    }
    
    private static String getRedisUserStatusKey(String userType,Long userId){
        return MRKAuthConstant.REDIS_USER_STATUS_KEY+userType+":"+userId;
    }
}
