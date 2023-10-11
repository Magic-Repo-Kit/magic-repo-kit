package com.magicrepokit.auth.service;

import com.magicrepokit.common.api.R;
import com.magicrepokit.user.feign.UserClient;
import com.magicrepokit.user.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理服务
 */
@Service
public class MRKUserDetailsServiceImpl implements UserDetailsService {
    @Autowired(required = false)
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //查询数据库
        R<UserInfo> result = userClient.userInfo(s, 1);
        UserInfo userInfo;
        if (result.isSuccess()) {
            userInfo = result.getData();
        }else{
            throw new UsernameNotFoundException(s + ":用户不存在");
        }
        List<GrantedAuthority> authorizes = new ArrayList<>();

        return new User(userInfo.getUser().getAccount(), userInfo.getUser().getPassword(), authorizes);
    }
}
