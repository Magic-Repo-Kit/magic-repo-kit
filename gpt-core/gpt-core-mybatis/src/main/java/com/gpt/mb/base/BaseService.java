package com.gpt.mb.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpt.common.api.PageResult;
import com.gpt.mb.utils.MyBatisUtils;
import org.apache.ibatis.annotations.Param;

/**
 * 基础Service-自定义扩展
 */
public interface BaseService<T> extends IService<T> {
    /**
     * 自定义分页
     */
    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        // MyBatis Plus 查询
        IPage<T> mpPage = MyBatisUtils.buildPage(pageParam);
        page(mpPage, queryWrapper);
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

}
