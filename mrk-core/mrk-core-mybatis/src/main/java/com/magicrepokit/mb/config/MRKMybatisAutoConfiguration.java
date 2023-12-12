package com.magicrepokit.mb.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.magicrepokit.mb.core.DefaultInsertHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MRKMybatisAutoConfiguration {
    @Bean
    public MetaObjectHandler defaultMetaObjectHandler(){
        return new DefaultInsertHandler(); // 自动填充参数类
    }
}
