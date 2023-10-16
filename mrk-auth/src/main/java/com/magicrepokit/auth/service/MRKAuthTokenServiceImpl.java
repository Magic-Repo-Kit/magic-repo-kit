package com.magicrepokit.auth.service;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.magicrepokit.auth.entity.vo.AuthAccessTokenVO;
import com.magicrepokit.user.entity.AuthClient;
import com.magicrepokit.user.vo.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 令牌服务
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Service
public class MRKAuthTokenServiceImpl implements MRKAuthTokenService {

    @Override
    public AuthAccessTokenVO createAccessToken(UserInfo userInfo, AuthClient client, List<String> scopes) {
        AuthAccessTokenVO accessTokenVO = new AuthAccessTokenVO();
        //请求token
        accessTokenVO.setAccessToken(generateToken());
        //刷新toke
        accessTokenVO.setRefreshToken(generateToken());
        //设置过期时间
        accessTokenVO.setExpiresIn(Long.valueOf(client.getAccessTokenValiditySeconds()));



        return null;
    }

    @Override
    public AuthAccessTokenVO refreshAccessToken(String refreshToken) {
        return null;
    }

    @Override
    public AuthAccessTokenVO removeAccessToken(String accessToken) {
        return null;
    }

    private static String generateToken() {
        return IdUtil.fastSimpleUUID();
    }
}
