package com.gpt.mb.core;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gpt.common.utils.AuthUtil;
import com.gpt.jwt.entity.GPTUser;
import com.gpt.mb.base.BaseEntity;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class DefaultInsertHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if(ObjectUtil.isNotNull(metaObject)&&metaObject.getOriginalObject() instanceof BaseEntity){
            BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();
            if(ObjectUtil.isNull(baseEntity.getCreateTime())){
                baseEntity.setCreateTime(LocalDateTime.now());
            }
            if(ObjectUtil.isNull(baseEntity.getUpdateTime())){
                baseEntity.setUpdateTime(LocalDateTime.now());
            }
            GPTUser user = AuthUtil.getUser();
            if(ObjectUtil.isNull(baseEntity.getCreateUser())&&ObjectUtil.isNotNull(user)){
                baseEntity.setCreateUser(user.getUserId());
            }
            if(ObjectUtil.isNull(baseEntity.getUpdateUser())&&ObjectUtil.isNotNull(user)){
                baseEntity.setUpdateUser(user.getUserId());
            }
            if(ObjectUtil.isNull(baseEntity.getDeleteFlag())){
                baseEntity.setDeleteFlag(0);
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if(ObjectUtil.isNotNull(metaObject)&&metaObject.getOriginalObject() instanceof BaseEntity){
            BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();
            if(ObjectUtil.isNull(baseEntity.getUpdateTime())){
                baseEntity.setUpdateTime(LocalDateTime.now());
            }
            GPTUser user = AuthUtil.getUser();
            if(ObjectUtil.isNull(baseEntity.getUpdateUser())&&ObjectUtil.isNotNull(user)){
                baseEntity.setUpdateUser(user.getUserId());
            }
        }
    }
}
