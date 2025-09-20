package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.ColumnDto;
import com.sidifensen.domain.dto.ColumnFilterDto;
import com.sidifensen.domain.entity.Column;
import com.sidifensen.domain.vo.ColumnVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.UserColumnManageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ColumnMapper;
import com.sidifensen.service.ColumnService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-26
 */
@Service
public class ColumnServiceImpl extends ServiceImpl<ColumnMapper, Column> implements ColumnService {

    @Resource
    private ColumnMapper columnMapper;

    @Override
    public List<ColumnVo> getColumnList() {
        Integer userId = SecurityUtils.getUserId();
        List<Column> columns = columnMapper.selectList(new LambdaQueryWrapper<Column>()
                .eq(userId != 0, Column::getUserId, userId)
                .orderByAsc(Column::getSort)); // 按排序值正序排列
        List<ColumnVo> columnVoList = BeanUtil.copyToList(columns, ColumnVo.class);
        return columnVoList;
    }

    @Override
    public void addColumn(ColumnDto columnDto) {
        Integer userId = SecurityUtils.getUserId();
        // 查找当前用户专栏的最大sort值
        Column column = columnMapper.selectOne(new LambdaQueryWrapper<Column>()
                .select(Column::getSort)
                .eq(Column::getUserId, userId)
                .orderByDesc(Column::getSort)
                .last("limit 1"));
        Integer maxSort = (column != null) ? column.getSort() : 0;
        columnDto.setSort(maxSort + 1);
        if (userId != 0) {
            columnDto.setUserId(userId);
        }
        columnMapper.insert(BeanUtil.copyProperties(columnDto, Column.class));
    }

    @Override
    public void updateColumn(ColumnDto columnDto) {
        Column column = columnMapper.selectById(columnDto.getId());
        if (!column.getUserId().equals(SecurityUtils.getUserId())) {
            throw new BlogException(BlogConstants.CannotHandleOthersColumn);
        }
        Column updateColumn = BeanUtil.copyProperties(columnDto, Column.class);
        if (columnMapper.updateById(updateColumn) <= 0) {
            throw new BlogException(BlogConstants.UpdateColumnError);
        }
    }

    @Override
    public void deleteColumn(Integer id) {
        Column column = columnMapper.selectById(id);
        if (!column.getUserId().equals(SecurityUtils.getUserId())) {
            throw new BlogException(BlogConstants.CannotHandleOthersColumn);
        }
        if (columnMapper.deleteById(id) <= 0) {
            throw new BlogException(BlogConstants.DeleteColumnError);
        }
    }

    @Override
    public PageVo<List<UserColumnManageVo>> getUserColumnManageList(Integer pageNum, Integer pageSize, ColumnFilterDto columnFilterDto) {
        Integer currentUserId = SecurityUtils.getUserId(); // 当前登录用户ID

        Page<Column> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Column> qw = new LambdaQueryWrapper<Column>()
                .eq(Column::getUserId, currentUserId) // 只查询当前用户的专栏
                .orderByAsc(Column::getSort); // 按排序值正序（排序值越小，排序越靠前）

        // 添加关键词搜索条件（搜索专栏名称和描述）
        if (ObjectUtil.isNotEmpty(columnFilterDto.getKeyword())) {
            qw.and(wrapper -> wrapper
                    .like(Column::getName, columnFilterDto.getKeyword()) // 专栏名称
                    .or()
                    .like(Column::getDescription, columnFilterDto.getKeyword())); // 专栏描述
        }

        // 添加展示状态筛选条件
        if (ObjectUtil.isNotEmpty(columnFilterDto.getShowStatus())) {
            qw.eq(Column::getShowStatus, columnFilterDto.getShowStatus());
        }

        // 添加根据年月查询的条件
        if (ObjectUtil.isNotEmpty(columnFilterDto.getYear()) || ObjectUtil.isNotEmpty(columnFilterDto.getMonth())) {
            if (ObjectUtil.isNotEmpty(columnFilterDto.getMonth())) {
                // 确定年份：如果有指定年份则使用，否则使用当前年份
                int year = ObjectUtil.isNotEmpty(columnFilterDto.getYear()) ?
                        columnFilterDto.getYear() : LocalDateTime.now().getYear();

                // 查询指定年份的指定月份
                LocalDateTime firstDayOfMonth = LocalDateTime.of(year, columnFilterDto.getMonth(), 1, 0, 0, 0);
                LocalDateTime lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth())
                        .with(LocalTime.MAX);
                qw.between(Column::getCreateTime, firstDayOfMonth, lastDayOfMonth);
            } else {
                // 如果只指定了年份，查询整年
                LocalDateTime firstDayOfYear = LocalDateTime.of(columnFilterDto.getYear(), 1, 1, 0, 0, 0);
                LocalDateTime lastDayOfYear = LocalDateTime.of(columnFilterDto.getYear(), 12, 31, 23, 59, 59);
                qw.between(Column::getCreateTime, firstDayOfYear, lastDayOfYear);
            }
        }

        List<Column> columns = columnMapper.selectPage(page, qw).getRecords();

        // 转换为 UserColumnManageVo
        List<UserColumnManageVo> userColumnManageVos = columns.stream().map(column -> {
            UserColumnManageVo vo = new UserColumnManageVo();

            // 设置专栏基本信息
            vo.setId(column.getId());
            vo.setName(column.getName());
            vo.setDescription(column.getDescription());
            vo.setCoverUrl(column.getCoverUrl());
            vo.setShowStatus(column.getShowStatus());
            vo.setFocusCount(column.getFocusCount());
            vo.setArticleCount(column.getArticleCount());
            vo.setSort(column.getSort());
            vo.setCreateTime(column.getCreateTime());
            vo.setUpdateTime(column.getUpdateTime());

            return vo;
        }).collect(Collectors.toList());

        return new PageVo<>(userColumnManageVos, page.getTotal());
    }
}
