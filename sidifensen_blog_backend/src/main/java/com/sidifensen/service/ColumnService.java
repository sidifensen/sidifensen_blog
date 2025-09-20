package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.ColumnDto;
import com.sidifensen.domain.dto.ColumnFilterDto;
import com.sidifensen.domain.entity.Column;
import com.sidifensen.domain.vo.ColumnVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.UserColumnManageVo;

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

    /**
     * 获取用户专栏列表(专栏管理)
     *
     * @param pageNum          页码
     * @param pageSize         页大小
     * @param columnFilterDto  专栏筛选条件
     * @return 用户专栏列表
     */
    PageVo<List<UserColumnManageVo>> getUserColumnManageList(Integer pageNum, Integer pageSize, ColumnFilterDto columnFilterDto);

}
