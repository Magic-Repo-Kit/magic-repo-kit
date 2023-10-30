package com.magicrepokit.auth.service;

import com.magicrepokit.auth.constant.MRKAuthConstant;
import com.magicrepokit.auth.constant.MRKI18N;
import com.magicrepokit.jwt.constant.UserType;
import com.magicrepokit.common.api.R;
import com.magicrepokit.common.utils.*;
import com.magicrepokit.jwt.utils.JWTUtil;
import com.magicrepokit.redis.utils.MRKRedisUtils;
import com.magicrepokit.system.feign.SystemClient;
import com.magicrepokit.system.entity.vo.UserInfoVO;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 用户管理服务
 */
@Service
public class MRKUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SystemClient systemClient;
    @Autowired
    private MRKRedisUtils mrkRedisUtils;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        HttpServletRequest request = WebUtil.getRequest();
        //获取用户类型
        String userType = request.getHeader(MRKAuthConstant.USER_TYPE);
        //获取密码
        String password = request.getParameter(MRKAuthConstant.PASSWORD);
        //请求类型
        String grantType = request.getParameter(MRKAuthConstant.GRANT_TYPE);

        if (StringUtil.isEmpty(userType)) {
            throw new UserDeniedAuthorizationException(MRKI18N.NOT_FOUND_USER_TYPE.getMessage());
        }
        UserType userTypeEnum = UserType.getByUserType(userType);
        if(userTypeEnum==null){
            throw new UserDeniedAuthorizationException(MRKI18N.NOT_FOUND_USER_TYPE.getMessage());
        }

        //判断token
        judgeRefreshToken(grantType,userType,request);

        //判断账户是否已锁定
        judgeFail(account);

        //查询数据库
        R<UserInfoVO> result = systemClient.userInfo(account);
        if (result.isSuccess()) {
            UserInfoVO userInfoVO = result.getData();
            if(ObjectUtil.isEmpty(userInfoVO)||ObjectUtil.isEmpty(userInfoVO.getUser())||(grantType.equals(MRKAuthConstant.PASSWORD)&&!BCrypt.checkpw(password, userInfoVO.getUser().getPassword()))){
                //用户错误次数+1
                setFailCount(account);
                throw new UsernameNotFoundException(MRKI18N.USER_NOT_FOUND.getMessage());
            }
            //匹配用户type
            if(!userInfoVO.getUser().getUserType().contains(userTypeEnum.getCode())){
                //用户错误次数+1
                setFailCount(account);
                throw new UsernameNotFoundException(MRKI18N.NOT_FOUND_USER_TYPE.getMessage());
            }


            ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            //成功清除错误次数
            delFailCount(account);
            return new MrkUserDetails(userInfoVO, grantedAuthorities);
        } else {
            //用户错误次数+1
            setFailCount(account);
            throw new UsernameNotFoundException(MRKI18N.USER_NOT_FOUND.getMessage());
        }
    }

    private void judgeRefreshToken(String grantType,String userType,HttpServletRequest request) {
        if (grantType.equals(MRKAuthConstant.REFRESH_TOKEN)) {
            String refreshToken = request.getParameter(MRKAuthConstant.REFRESH_TOKEN);
            //判断令牌的合法性
            Claims claims = JWTUtil.parseJWT(refreshToken);
            if(claims==null){
                throw new UserDeniedAuthorizationException(MRKI18N.UNKNOWN_REFRESH_TOKEN.getMessage());
            }
            Long userId = Long.valueOf(String.valueOf(claims.get("user_id")));
            String token = JWTUtil.getRefreshToken(userId, userType);
            if(token==null||!token.equalsIgnoreCase(refreshToken)){
                throw new UserDeniedAuthorizationException(MRKI18N.INVALID_TOKEN.getMessage());
            }
        }
    }

    /**
     * 删除锁定
     * @param account 账户
     */
    private void delFailCount(String account) {
        mrkRedisUtils.del(MRKAuthConstant.getFailRedisKey(account));
    }

    /**
     * 锁定值+1
     * @param account 账户
     */
    private void setFailCount(String account) {
        mrkRedisUtils.incr(MRKAuthConstant.getFailRedisKey(account), 1);
        mrkRedisUtils.expire(MRKAuthConstant.getFailRedisKey(account), 5 * 60);
    }

    /**
     * 判断是否被锁定
     *
     * @param account 账户
     */
    private void judgeFail(String account) {
        int count = MRKUtil.toInt(mrkRedisUtils.get(MRKAuthConstant.getFailRedisKey(account)), 0);
        if (count >= MRKAuthConstant.FAIL_COUNT) {
            throw new UserDeniedAuthorizationException(MRKI18N.USER_IS_LOCKED.getMessage());
        }
    }
}
