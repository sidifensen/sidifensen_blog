package com.sidifensen.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sidifensen.domain.vo.PageVo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 分页工具类
 *
 * @author sidifensen
 * @since 2025-08-24
 */
@Slf4j
public class PageUtils {

    /**
     * 将分页结果和记录列表转换为 PageVo
     * 注意：项目的 PageVo 泛型参数是 List<Vo>，不是单独的 Vo
     */
    @SuppressWarnings("unchecked")
    public static <R> PageVo<List<R>> buildPageVo(Page<?> page, List<R> records) {
        return new PageVo(records, page.getTotal());
    }

    /**
     * 构建分页对象，带参数校验
     */
    public static <T> Page<T> buildPage(Integer pageNum, Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            pageSize = 1;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        return new Page<>(pageNum, pageSize);
    }

}
