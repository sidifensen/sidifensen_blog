package com.sidifensen.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author sidifensen
 * @since 2025-08-24
 */
@Data
@AllArgsConstructor
public class PageVo<T> {

    // 分页数据
    private T data;
    // 总数
    private Long total;

}
