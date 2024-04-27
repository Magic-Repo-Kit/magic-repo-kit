package com.magicrepokit.common.utils;

import cn.hutool.core.util.StrUtil;
import com.magicrepokit.jwt.entity.GPTUser;
import com.magicrepokit.jwt.utils.JWTUtil;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

public class AuthUtil {
    private static final String AUTHORIZATION = "Authorization";
    private static final String USER_TYPE = "User-Type";
    private static final String USER_ID = "user_id";

    public static GPTUser getUser(){
        HttpServletRequest request = WebUtil.getRequest();
        if (request == null) {
            return null;
        }
        return getUser(request);
    }
    public static GPTUser getUser(HttpServletRequest request){
        Claims claims = getClaims(request);
        if(claims==null){
            return null;
        }
        Long userId = Long.valueOf(String.valueOf(claims.get(USER_ID)));
        String name = String.valueOf(claims.get("name"));
        String realName = String.valueOf(claims.get("realName"));
        String account = String.valueOf(claims.get("account"));
        String deptId = String.valueOf(claims.get("deptId"));
        String postId = String.valueOf(claims.get("postId"));
        String roleId = String.valueOf(claims.get("roleId"));
        return new GPTUser(userId,name,realName,account,deptId,postId,roleId);
    }

    /**
     * 获取Claims
     *
     * @param request request
     * @return Claims
     */
    public static Claims getClaims(HttpServletRequest request) {
        String auth = request.getHeader(AUTHORIZATION);
        String userType = request.getHeader(USER_TYPE);
        if (StrUtil.isBlank(userType)) {
            return null;
        }
        Claims claims = null;
        String token = null;
        // 获取 请求头 参数
        if (StrUtil.isNotBlank(auth)) {
            token = JWTUtil.getToken(auth);
        }
        // 获取 Token 值
        if (StrUtil.isNotBlank(token)) {
            claims = JWTUtil.parseJWT(token);
        }
        // 判断 Token 状态
        if (claims != null) {
            String userIdStr = String.valueOf(claims.get(USER_ID));
            Long userId = Long.valueOf(userIdStr);
            String accessToken = JWTUtil.getAccessToken(userId, userType);
            if (!token.equals(accessToken)) {
                return null;
            }
        }
        return claims;
    }
}
