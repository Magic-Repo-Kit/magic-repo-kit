package com.gpt.auth.service;

import cn.hutool.core.util.ObjectUtil;
import com.gpt.auth.constant.GPTAuthConstant;
import com.gpt.auth.constant.GPTI18N;
import com.gpt.common.utils.GPTUtil;
import com.gpt.common.utils.StringUtil;
import com.gpt.common.utils.WebUtil;
import com.gpt.jwt.constant.UserType;
import com.gpt.common.api.R;
import com.gpt.jwt.utils.JWTUtil;
import com.gpt.redis.utils.GPTRedisUtils;
import com.gpt.system.feign.ISystemClient;
import com.gpt.system.vo.user.UserInfoVO;
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
public class GPTUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private ISystemClient ISystemClient;
    @Autowired
    private GPTRedisUtils GPTRedisUtils;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        HttpServletRequest request = WebUtil.getRequest();
        //获取用户类型
        String userType = request.getHeader(GPTAuthConstant.USER_TYPE);
        //获取密码
        String password = request.getParameter(GPTAuthConstant.PASSWORD);
        //请求类型
        String grantType = request.getParameter(GPTAuthConstant.GRANT_TYPE);

        if (StringUtil.isEmpty(userType)) {
            throw new UserDeniedAuthorizationException(GPTI18N.NOT_FOUND_USER_TYPE.getMessage());
        }
        UserType userTypeEnum = UserType.getByUserType(userType);
        if(userTypeEnum==null){
            throw new UserDeniedAuthorizationException(GPTI18N.NOT_FOUND_USER_TYPE.getMessage());
        }

        //判断token
        judgeRefreshToken(grantType,userType,request);

        //判断账户是否已锁定
        judgeFail(account);

        //查询数据库
        R<UserInfoVO> result = ISystemClient.userInfo(account);
        if (result.isSuccess()) {
            UserInfoVO userInfoVO = result.getData();
            if(ObjectUtil.isEmpty(userInfoVO)||ObjectUtil.isEmpty(userInfoVO.getUser())||(grantType.equals(GPTAuthConstant.PASSWORD)&&!BCrypt.checkpw(password, userInfoVO.getUser().getPassword()))){
                //用户错误次数+1
                setFailCount(account);
                throw new UsernameNotFoundException(GPTI18N.USER_NOT_FOUND.getMessage());
            }
            //匹配用户type
            if(!userInfoVO.getUser().getUserType().contains(userTypeEnum.getCode())){
                //用户错误次数+1
                setFailCount(account);
                throw new UsernameNotFoundException(GPTI18N.NOT_FOUND_USER_TYPE.getMessage());
            }


            ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            //成功清除错误次数
            delFailCount(account);
            return new GPTUserDetails(userInfoVO, grantedAuthorities);
        } else {
            //用户错误次数+1
            setFailCount(account);
            throw new UsernameNotFoundException(GPTI18N.USER_NOT_FOUND.getMessage());
        }
    }

    private void judgeRefreshToken(String grantType,String userType,HttpServletRequest request) {
        if (grantType.equals(GPTAuthConstant.REFRESH_TOKEN)) {
            String refreshToken = request.getParameter(GPTAuthConstant.REFRESH_TOKEN);
            //判断令牌的合法性
            Claims claims = JWTUtil.parseJWT(refreshToken);
            if(claims==null){
                throw new UserDeniedAuthorizationException(GPTI18N.UNKNOWN_REFRESH_TOKEN.getMessage());
            }
            Long userId = Long.valueOf(String.valueOf(claims.get("user_id")));
            String token = JWTUtil.getRefreshToken(userId, userType);
            if(token==null||!token.equalsIgnoreCase(refreshToken)){
                throw new UserDeniedAuthorizationException(GPTI18N.INVALID_TOKEN.getMessage());
            }
        }
    }

    /**
     * 删除锁定
     * @param account 账户
     */
    private void delFailCount(String account) {
        GPTRedisUtils.del(GPTAuthConstant.getFailRedisKey(account));
    }

    /**
     * 锁定值+1
     * @param account 账户
     */
    private void setFailCount(String account) {
        GPTRedisUtils.incr(GPTAuthConstant.getFailRedisKey(account), 1);
        GPTRedisUtils.expire(GPTAuthConstant.getFailRedisKey(account), 5 * 60);
    }

    /**
     * 判断是否被锁定
     *
     * @param account 账户
     */
    private void judgeFail(String account) {
        int count = GPTUtil.toInt(GPTRedisUtils.get(GPTAuthConstant.getFailRedisKey(account)), 0);
        if (count >= GPTAuthConstant.FAIL_COUNT) {
            throw new UserDeniedAuthorizationException(GPTI18N.USER_IS_LOCKED.getMessage());
        }
    }
}
