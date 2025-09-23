package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.ColumnArticleSortDto;
import com.sidifensen.domain.dto.ColumnDto;
import com.sidifensen.domain.dto.ColumnFilterDto;
import com.sidifensen.domain.dto.ColumnSearchDto;
import com.sidifensen.domain.entity.Column;
import com.sidifensen.domain.vo.ColumnDetailVo;
import com.sidifensen.domain.vo.ColumnStatisticsVo;
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

    // 根据用户ID获取专栏列表
    PageVo<List<ColumnVo>> getColumnListByUserId(Integer userId, Integer pageNum, Integer pageSize);


    // 获取专栏详情（包含专栏内的文章列表）
    ColumnDetailVo getColumnDetail(Integer columnId);

    // 搜索专栏列表
    PageVo<List<ColumnVo>> searchColumnList(Integer pageNum, Integer pageSize, String keyword);

    // 获取用户专栏列表(专栏管理)
    PageVo<List<UserColumnManageVo>> getUserColumnManageList(Integer pageNum, Integer pageSize, ColumnFilterDto columnFilterDto);

    // 增加专栏
    void addColumn(ColumnDto columnDto);

    // 修改专栏
    void updateColumn(ColumnDto columnDto);

    // 删除专栏
    void deleteColumn(Integer id);

    // 调整专栏内文章排序
    void updateColumnArticleSort(Integer columnId, List<ColumnArticleSortDto> sortList);

    // 从专栏中删除文章
    void removeArticleFromColumn(Integer columnId, Integer articleId);

    // 管理员获取专栏列表
    List<UserColumnManageVo> adminGetColumnList(ColumnFilterDto columnFilterDto);

    // 管理员根据用户ID获取专栏列表
    List<UserColumnManageVo> adminGetColumnsByUserId(Integer userId);
    
    // 管理员搜索专栏
    List<UserColumnManageVo> adminSearchColumn(ColumnSearchDto columnSearchDto);

    // 管理员审核专栏
    void adminExamineColumn(Integer columnId, Integer examineStatus);

    // 管理员批量审核专栏
    void adminBatchExamineColumn(List<Integer> columnIds, Integer examineStatus);

    // 管理员更新专栏
    void adminUpdateColumn(ColumnDto columnDto);

    // 管理员删除专栏
    void adminDeleteColumn(Integer columnId);

    // 管理员批量删除专栏
    void adminBatchDeleteColumn(List<Integer> columnIds);

    // 管理员获取专栏统计数据
    ColumnStatisticsVo getColumnStatistics();

}
