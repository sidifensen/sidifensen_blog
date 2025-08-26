package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.ColumnDto;
import com.sidifensen.domain.entity.Column;
import com.sidifensen.domain.vo.ColumnVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ColumnMapper;
import com.sidifensen.service.ColumnService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<Column> columns = columnMapper.selectList(new LambdaQueryWrapper<Column>().eq(userId != 0, Column::getUserId, userId));
        List<ColumnVo> columnVoList = BeanUtil.copyToList(columns, ColumnVo.class);
        return columnVoList;
    }

    @Override
    public void addColumn(ColumnDto columnDto) {
        Integer userId = SecurityUtils.getUserId();
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
}
