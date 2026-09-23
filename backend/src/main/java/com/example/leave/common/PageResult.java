package com.example.leave.common;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 分页查询结果封装
 *
 * @param <T> 列表数据类型
 */
@Data
public class PageResult<T> {

    /** 当前页码 */
    private Integer page;

    /** 每页条数 */
    private Integer pageSize;

    /** 总记录数 */
    private Long total;

    /** 总页数 */
    private Integer pages;

    /** 数据列表 */
    private List<T> list;

    private PageResult() {}

    /**
     * 全参构造
     */
    public PageResult(Integer page, Integer pageSize, Long total, List<T> list) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = (total == null || total == 0 || pageSize == null || pageSize == 0)
                ? 0
                : (int) Math.ceil((double) total / pageSize);
        this.list = list != null ? list : Collections.emptyList();
    }

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(Integer page, Integer pageSize, Long total, List<T> list) {
        return new PageResult<>(page, pageSize, total, list);
    }

    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> empty(Integer page, Integer pageSize) {
        return new PageResult<>(page, pageSize, 0L, Collections.emptyList());
    }
}
