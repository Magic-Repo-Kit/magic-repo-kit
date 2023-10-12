package com.magicrepokit.auth.service;

import com.magicrepokit.auth.constant.MRKAuthConstant;
import com.magicrepokit.auth.constant.MRKI18N;
import com.magicrepokit.common.api.R;
import com.magicrepokit.common.utils.ObjectUtil;
import com.magicrepokit.common.utils.StringUtil;
import com.magicrepokit.common.utils.WebUtil;
import com.magicrepokit.redis.utils.MRKRedisUtils;
import com.magicrepokit.user.feign.UserClient;
import com.magicrepokit.user.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private UserClient userClient;
    @Autowired
    private MRKRedisUtils mrkRedisUtils;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        HttpServletRequest request = WebUtil.getRequest();
        //获取用户类型
        String userType = request.getParameter(MRKAuthConstant.USER_TYPE);
        if (StringUtil.isEmpty(userType)) {
            throw new UserDeniedAuthorizationException(MRKI18N.NOT_FOUND_USER_TYPE.getMessage());
        }
        //判断账户是否已锁定
        judgeFail(account);
        //查询数据库
        R<UserInfo> result = userClient.userInfo(account, 1);
        if (result.isSuccess()) {
            UserInfo userInfo = result.getData();
            ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            //成功清除错误次数
            delFailCount(account);
            return new MrkUserDetails(userInfo, grantedAuthorities);
        } else {
            //用户错误次数+1
            setFailCount(account);
            throw new UsernameNotFoundException(MRKI18N.USER_NOT_FOUND.getMessage());
        }
    }

    private void delFailCount(String account) {
        mrkRedisUtils.del(MRKAuthConstant.getFailRedisKey(account));
    }

    private void setFailCount(String account) {
        mrkRedisUtils.incr(MRKAuthConstant.getFailRedisKey(account), 1);
        mrkRedisUtils.expire(MRKAuthConstant.getFailRedisKey(account), 5 * 60);
    }

    /**
     * 判断是否被锁定
     *
     * @param account
     */
    private void judgeFail(String account) {
        Integer count = (Integer) mrkRedisUtils.get(MRKAuthConstant.getFailRedisKey(account));

        if (ObjectUtil.isNotEmpty(count) && count >= MRKAuthConstant.FAIL_COUNT) {
            throw new UserDeniedAuthorizationException(MRKI18N.USER_IS_LOCKED.getMessage());
        }
    }
}
