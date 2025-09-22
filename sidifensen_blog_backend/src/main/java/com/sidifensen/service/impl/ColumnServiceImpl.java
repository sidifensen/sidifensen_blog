package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.ColumnArticleSortDto;
import com.sidifensen.domain.dto.ColumnDto;
import com.sidifensen.domain.dto.ColumnFilterDto;
import com.sidifensen.domain.dto.ColumnSearchDto;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.ArticleColumn;
import com.sidifensen.domain.entity.Column;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.ShowStatusEnum;
import com.sidifensen.domain.vo.*;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ArticleColumnMapper;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.ColumnMapper;
import com.sidifensen.service.ColumnService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
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

    @Resource
    private ArticleColumnMapper articleColumnMapper;

    @Resource
    private ArticleMapper articleMapper;


    @Override
    public List<ColumnVo> getColumnList() {
        Integer userId = SecurityUtils.getUserId();
        List<Column> columns = columnMapper.selectList(new LambdaQueryWrapper<Column>()
                .eq(userId != 0, Column::getUserId, userId)
                .eq(Column::getExamineStatus, ExamineStatusEnum.PASS.getCode()) // 只返回审核通过的专栏
                .orderByAsc(Column::getSort)); // 按排序值正序排列
        List<ColumnVo> columnVoList = BeanUtil.copyToList(columns, ColumnVo.class);
        return columnVoList;
    }

    @Override
    public PageVo<List<ColumnVo>> getColumnListByUserId(Integer userId, Integer pageNum, Integer pageSize) {
        Page<Column> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Column> qw = new LambdaQueryWrapper<Column>()
                .eq(Column::getUserId, userId)
                .eq(Column::getShowStatus, ShowStatusEnum.PUBLIC.getCode())
                .eq(Column::getExamineStatus, ExamineStatusEnum.PASS.getCode()) // 只返回审核通过的专栏
                .orderByAsc(Column::getSort); // 按排序值正序排列

        List<Column> columns = columnMapper.selectPage(page, qw).getRecords();
        List<ColumnVo> columnVoList = BeanUtil.copyToList(columns, ColumnVo.class);
        return new PageVo<>(columnVoList, page.getTotal());
    }


    @Override
    public ColumnDetailVo getColumnDetail(Integer columnId) {
        // 1. 获取专栏基本信息
        Column column = columnMapper.selectById(columnId);
        if (column == null) {
            throw new BlogException(BlogConstants.NotFoundColumn);
        }

        // 2. 检查专栏权限（只能查看审核通过且公开的专栏，或者自己的专栏）
        Integer currentUserId = SecurityUtils.getUserId();
        boolean canAccess = false;

        // 如果是自己的专栏，可以访问
        if (currentUserId != null && currentUserId.equals(column.getUserId())) {
            canAccess = true;
        } else if (ExamineStatusEnum.PASS.getCode().equals(column.getExamineStatus()) && column.getShowStatus() == 0) {
            // 如果是审核通过且公开的专栏，可以访问
            canAccess = true;
        }

        if (!canAccess) {
            throw new BlogException(BlogConstants.NotFoundColumn);
        }

        // 3. 构建专栏详情基本信息
        ColumnDetailVo columnDetailVo = BeanUtil.copyProperties(column, ColumnDetailVo.class);

        // 4. 获取专栏内的文章列表（从文章专栏关联表查询）
        List<ArticleColumn> articleColumns = articleColumnMapper.selectList(
                new LambdaQueryWrapper<ArticleColumn>()
                        .eq(ArticleColumn::getColumnId, columnId)
                        .orderByAsc(ArticleColumn::getSort) // 按专栏内排序
        );

        if (ObjectUtil.isNotEmpty(articleColumns)) {
            // 获取文章ID列表
            List<Integer> articleIds = articleColumns.stream()
                    .map(ArticleColumn::getArticleId)
                    .toList();

            // 构建文章查询条件
            LambdaQueryWrapper<Article> articleQueryWrapper = new LambdaQueryWrapper<Article>()
                    .in(Article::getId, articleIds);
            
            // 如果不是专栏作者，只能看到审核通过的文章
            if (currentUserId == null || !currentUserId.equals(column.getUserId())) {
                articleQueryWrapper.eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode());
            }

            List<Article> articles = articleMapper.selectList(articleQueryWrapper);

            // 构建文章VO列表，保持专栏内的排序
            List<ColumnArticleVo> columnArticles = articleColumns.stream()
                    .map(articleColumn -> {
                        // 找到对应的文章
                        Article article = articles.stream()
                                .filter(a -> a.getId().equals(articleColumn.getArticleId()))
                                .findFirst()
                                .orElse(null);

                        if (article != null) {
                            ColumnArticleVo columnArticleVo = BeanUtil.copyProperties(article, ColumnArticleVo.class);
                            columnArticleVo.setSort(articleColumn.getSort()); // 设置在专栏中的排序
                            return columnArticleVo;
                        }
                        return null;
                    })
                    .filter(ObjectUtil::isNotNull) // 过滤空值
                    .toList();

            columnDetailVo.setArticles(columnArticles);
        }

        return columnDetailVo;
    }

    @Override
    public PageVo<List<ColumnVo>> searchColumnList(Integer pageNum, Integer pageSize, String keyword) {
        Page<Column> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Column> qw = new LambdaQueryWrapper<Column>()
                .eq(Column::getExamineStatus, ExamineStatusEnum.PASS.getCode()) // 只返回审核通过的专栏
                .eq(Column::getShowStatus, 0) // 只返回公开的专栏
                .orderByDesc(Column::getCreateTime); // 按创建时间倒序排列

        // 添加关键词搜索条件（搜索专栏名称和描述）
        if (ObjectUtil.isNotEmpty(keyword)) {
            qw.and(wrapper -> wrapper
                    .like(Column::getName, keyword) // 专栏名称
                    .or()
                    .like(Column::getDescription, keyword)); // 专栏描述
        }

        List<Column> columns = columnMapper.selectPage(page, qw).getRecords();
        List<ColumnVo> columnVoList = BeanUtil.copyToList(columns, ColumnVo.class);
        return new PageVo<>(columnVoList, page.getTotal());
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

        // 如果修改了标题、描述或封面，重新设为待审核状态
        boolean nameChanged = !ObjectUtil.equal(column.getName(), columnDto.getName());
        boolean descriptionChanged = !ObjectUtil.equal(column.getDescription(), columnDto.getDescription());
        boolean coverUrlChanged = !ObjectUtil.equal(column.getCoverUrl(), columnDto.getCoverUrl());

        if (nameChanged || descriptionChanged || coverUrlChanged) {
            updateColumn.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
        }

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
    public void updateColumnArticleSort(Integer columnId, List<ColumnArticleSortDto> sortList) {
        // 1. 验证专栏权限
        Column column = columnMapper.selectById(columnId);
        if (column == null) {
            throw new BlogException(BlogConstants.NotFoundColumn);
        }

        Integer currentUserId = SecurityUtils.getUserId();
        if (!column.getUserId().equals(currentUserId)) {
            throw new BlogException(BlogConstants.CannotHandleOthersColumn);
        }

        // 2. 验证文章是否都在该专栏中
        List<Integer> articleIds = sortList.stream()
                .map(ColumnArticleSortDto::getArticleId)
                .toList();

        List<ArticleColumn> existingArticleColumns = articleColumnMapper.selectList(
                new LambdaQueryWrapper<ArticleColumn>()
                        .eq(ArticleColumn::getColumnId, columnId)
                        .in(ArticleColumn::getArticleId, articleIds)
        );

        if (existingArticleColumns.size() != articleIds.size()) {
            throw new BlogException(BlogConstants.ArticleNotInColumn);
        }

        // 3. 批量更新排序
        for (ColumnArticleSortDto sortDto : sortList) {
            articleColumnMapper.update(null,
                    new LambdaUpdateWrapper<ArticleColumn>()
                            .eq(ArticleColumn::getColumnId, columnId)
                            .eq(ArticleColumn::getArticleId, sortDto.getArticleId())
                            .set(ArticleColumn::getSort, sortDto.getSort())
            );
        }
    }

    @Override
    public void removeArticleFromColumn(Integer columnId, Integer articleId) {
        // 1. 验证专栏权限
        Column column = columnMapper.selectById(columnId);
        if (column == null) {
            throw new BlogException(BlogConstants.NotFoundColumn);
        }

        Integer currentUserId = SecurityUtils.getUserId();
        if (!column.getUserId().equals(currentUserId)) {
            throw new BlogException(BlogConstants.CannotHandleOthersColumn);
        }

        // 2. 验证文章是否在该专栏中
        ArticleColumn articleColumn = articleColumnMapper.selectOne(
                new LambdaQueryWrapper<ArticleColumn>()
                        .eq(ArticleColumn::getColumnId, columnId)
                        .eq(ArticleColumn::getArticleId, articleId)
        );

        if (articleColumn == null) {
            throw new BlogException(BlogConstants.ArticleNotInColumn);
        }

        // 3. 删除文章专栏关联关系
        int deleteResult = articleColumnMapper.delete(
                new LambdaQueryWrapper<ArticleColumn>()
                        .eq(ArticleColumn::getColumnId, columnId)
                        .eq(ArticleColumn::getArticleId, articleId)
        );

        if (deleteResult <= 0) {
            throw new BlogException(BlogConstants.RemoveArticleFromColumnError);
        }

        // 4. 更新专栏文章数量
        if (column.getArticleCount() > 0) {
            column.setArticleCount(column.getArticleCount() - 1);
            columnMapper.updateById(column);
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

        // 添加审核状态筛选条件
        if (ObjectUtil.isNotEmpty(columnFilterDto.getExamineStatus())) {
            qw.eq(Column::getExamineStatus, columnFilterDto.getExamineStatus());
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
        List<UserColumnManageVo> userColumnManageVos = BeanUtil.copyToList(columns, UserColumnManageVo.class);

        return new PageVo<>(userColumnManageVos, page.getTotal());
    }


    @Override
    public List<UserColumnManageVo> adminGetColumnList(ColumnFilterDto columnFilterDto) {
        LambdaQueryWrapper<Column> qw = new LambdaQueryWrapper<Column>()
                .orderByDesc(Column::getCreateTime); // 按创建时间倒序排列

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

        // 添加审核状态筛选条件
        if (ObjectUtil.isNotEmpty(columnFilterDto.getExamineStatus())) {
            qw.eq(Column::getExamineStatus, columnFilterDto.getExamineStatus());
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

        List<Column> columns = columnMapper.selectList(qw);

        // 转换为 UserColumnManageVo
        List<UserColumnManageVo> userColumnManageVos = BeanUtil.copyToList(columns, UserColumnManageVo.class);

        return userColumnManageVos;
    }

    @Override
    public List<UserColumnManageVo> adminGetColumnsByUserId(Integer userId) {
        LambdaQueryWrapper<Column> qw = new LambdaQueryWrapper<Column>()
                .eq(Column::getUserId, userId)
                .orderByDesc(Column::getCreateTime); // 按创建时间倒序排列

        List<Column> columns = columnMapper.selectList(qw);

        // 转换为 UserColumnManageVo
        List<UserColumnManageVo> userColumnManageVos = BeanUtil.copyToList(columns, UserColumnManageVo.class);

        return userColumnManageVos;
    }

    @Override
    public List<UserColumnManageVo> adminSearchColumn(ColumnSearchDto columnSearchDto) {
        LambdaQueryWrapper<Column> qw = new LambdaQueryWrapper<Column>()
                .orderByDesc(Column::getCreateTime); // 按创建时间倒序排列

        // 添加审核状态条件
        if (ObjectUtil.isNotEmpty(columnSearchDto.getExamineStatus())) {
            qw.eq(Column::getExamineStatus, columnSearchDto.getExamineStatus());
        }

        // 添加用户ID条件
        if (ObjectUtil.isNotEmpty(columnSearchDto.getUserId())) {
            qw.eq(Column::getUserId, columnSearchDto.getUserId());
        }

        // 添加展示状态条件
        if (ObjectUtil.isNotEmpty(columnSearchDto.getShowStatus())) {
            qw.eq(Column::getShowStatus, columnSearchDto.getShowStatus());
        }

        // 添加关键词搜索条件（搜索专栏名称和描述）
        if (ObjectUtil.isNotEmpty(columnSearchDto.getKeyword())) {
            qw.and(wrapper -> wrapper
                    .like(Column::getName, columnSearchDto.getKeyword()) // 专栏名称
                    .or()
                    .like(Column::getDescription, columnSearchDto.getKeyword())); // 专栏描述
        }

        // 添加创建时间范围条件
        if (ObjectUtil.isNotEmpty(columnSearchDto.getCreateTimeStart())) {
            qw.ge(Column::getCreateTime, columnSearchDto.getCreateTimeStart());
        }
        if (ObjectUtil.isNotEmpty(columnSearchDto.getCreateTimeEnd())) {
            qw.le(Column::getCreateTime, columnSearchDto.getCreateTimeEnd());
        }

        List<Column> columns = columnMapper.selectList(qw);

        // 转换为 UserColumnManageVo
        List<UserColumnManageVo> userColumnManageVos = BeanUtil.copyToList(columns, UserColumnManageVo.class);

        return userColumnManageVos;
    }

    @Override
    public void adminExamineColumn(Integer columnId, Integer examineStatus) {
        Column column = columnMapper.selectById(columnId);
        if (ObjectUtil.isEmpty(column)) {
            throw new BlogException(BlogConstants.NotFoundColumn);
        }

        // 验证审核状态
        if (!ExamineStatusEnum.PASS.getCode().equals(examineStatus) &&
                !ExamineStatusEnum.NO_PASS.getCode().equals(examineStatus)) {
            throw new BlogException(BlogConstants.ExamineStatusError);
        }

        Column updateColumn = new Column();
        updateColumn.setId(columnId);
        updateColumn.setExamineStatus(examineStatus);

        if (columnMapper.updateById(updateColumn) <= 0) {
            throw new BlogException(BlogConstants.ExamineColumnError);
        }
    }

    @Override
    public void adminBatchExamineColumn(List<Integer> columnIds, Integer examineStatus) {
        if (ObjectUtil.isEmpty(columnIds)) {
            throw new BlogException(BlogConstants.ColumnIdsEmpty);
        }

        // 验证审核状态
        if (!ExamineStatusEnum.PASS.getCode().equals(examineStatus) &&
                !ExamineStatusEnum.NO_PASS.getCode().equals(examineStatus)) {
            throw new BlogException(BlogConstants.ExamineStatusError);
        }

        // 验证专栏是否存在
        List<Column> columns = columnMapper.selectBatchIds(columnIds);
        if (ObjectUtil.isEmpty(columns) || columns.size() != columnIds.size()) {
            throw new BlogException(BlogConstants.NotFoundColumn);
        }

        // 批量更新审核状态
        LambdaUpdateWrapper<Column> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Column::getId, columnIds)
                .set(Column::getExamineStatus, examineStatus);

        if (columnMapper.update(null, updateWrapper) <= 0) {
            throw new BlogException(BlogConstants.BatchExamineColumnError);
        }
    }

    @Override
    public void adminDeleteColumn(Integer columnId) {
        Column column = columnMapper.selectById(columnId);
        if (ObjectUtil.isEmpty(column)) {
            throw new BlogException(BlogConstants.NotFoundColumn);
        }

        if (columnMapper.deleteById(columnId) <= 0) {
            throw new BlogException(BlogConstants.DeleteColumnError);
        }
    }

    @Override
    public void adminBatchDeleteColumn(List<Integer> columnIds) {
        if (ObjectUtil.isEmpty(columnIds)) {
            throw new BlogException(BlogConstants.ColumnIdsEmpty);
        }

        // 验证专栏是否存在
        List<Column> columns = columnMapper.selectBatchIds(columnIds);
        if (ObjectUtil.isEmpty(columns)) {
            throw new BlogException(BlogConstants.NotFoundColumn);
        }

        // 批量删除专栏
        if (columnMapper.deleteBatchIds(columnIds) <= 0) {
            throw new BlogException(BlogConstants.BatchDeleteColumnError);
        }
    }
}
