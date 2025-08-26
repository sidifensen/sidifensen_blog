package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.ColumnDto;
import com.sidifensen.domain.entity.Column;
import com.sidifensen.domain.vo.ColumnVo;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-08-26
 */
public interface ColumnService extends IService<Column> {

    // 获取专栏列表
    List<ColumnVo> getColumnList();

    // 增加专栏
    void addColumn(ColumnDto columnDto);

    // 修改专栏
    void updateColumn(ColumnDto columnDto);

    // 删除专栏
    void deleteColumn(Integer id);

}
