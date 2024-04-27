package com.magicrepokit.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.entity.KnowledgeDetail;
import com.magicrepokit.chat.mapper.KnowledgeDetailMapper;
import com.magicrepokit.chat.service.IKnowledgeDetailService;
import com.magicrepokit.common.utils.AuthUtil;
import com.magicrepokit.jwt.entity.GPTUser;
import com.magicrepokit.mb.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeDetailServiceImpl extends BaseServiceImpl<KnowledgeDetailMapper, KnowledgeDetail> implements IKnowledgeDetailService {
    @Override
    public List<KnowledgeDetail> listNotCompleted() {
        GPTUser user = AuthUtil.getUser();
        if (ObjectUtil.isEmpty(user)) {
            return null;
        }
        return listNotCompleted(user.getUserId());
    }

    @Override
    public List<KnowledgeDetail> listByKnowledgeId(Long knowledgeId) {
        return this.list(new LambdaQueryWrapper<KnowledgeDetail>()
                .eq(KnowledgeDetail::getKnowledgeId, knowledgeId)
        );
    }

    @Override
    public boolean checkIsFileExist(Long knowledgeId, String fileName) {
        return this.count(new LambdaQueryWrapper<KnowledgeDetail>().eq(KnowledgeDetail::getKnowledgeId, knowledgeId).eq(KnowledgeDetail::getName, fileName)) > 0;
    }

    @Override
    public boolean checkHasFile(Long knowledgeId) {
        return this.count(new LambdaQueryWrapper<KnowledgeDetail>().eq(KnowledgeDetail::getKnowledgeId, knowledgeId)) > 0;
    }

    @Override
    public boolean checkIsFileBatchExist(Long id, List<String> fileNames) {
        return this.count(new LambdaQueryWrapper<>(KnowledgeDetail.class)
                .eq(KnowledgeDetail::getKnowledgeId, id)
                .in(KnowledgeDetail::getName, fileNames))> 0;
    }

    @Override
    public boolean deleteByKnowledgeIds(List<Long> knowledgeIdList) {
        if (ObjectUtil.isEmpty(knowledgeIdList)) {
            return false;
        }
        return this.remove(new LambdaQueryWrapper<KnowledgeDetail>().in(KnowledgeDetail::getKnowledgeId, knowledgeIdList));
    }

    private List<KnowledgeDetail> listNotCompleted(Long userId) {
        return this.list(new LambdaQueryWrapper<KnowledgeDetail>()
                .eq(KnowledgeDetail::getCreateUser, userId)
                .ne(KnowledgeDetail::getStatus, 4)
        );
    }
}
