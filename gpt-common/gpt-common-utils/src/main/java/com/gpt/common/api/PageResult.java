package com.gpt.common.api;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Data
public final class PageResult<T> implements Serializable {

    private List<T> list;


    private Long total;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }

    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<>(total);
    }

    public  <R> PageResult<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.list.stream().map(mapper).collect(toList());
        return new PageResult<>(collect, this.total);
    }

}
