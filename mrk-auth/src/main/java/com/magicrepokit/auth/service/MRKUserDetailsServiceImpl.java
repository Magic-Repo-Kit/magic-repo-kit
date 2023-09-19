package com.magicrepokit.auth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MRKUserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //查询数据库
        List<GrantedAuthority> authorizes = new ArrayList<>();
        if(s.equals("zhangsan")){
            User user = new User("zhangsan","$2a$10$zJcY6m6UGNlxgZ8aiXoe1uOk3F6oplmkU9a/q/cMeDdh82UD3MdlK",authorizes);
            return user;
        }else {
         throw new UsernameNotFoundException(s+":用户不存在");
        }
    }
}
