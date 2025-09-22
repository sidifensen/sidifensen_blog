package com.sidifensen.controller;


import com.sidifensen.domain.dto.ColumnArticleSortDto;
import com.sidifensen.domain.dto.ColumnDto;
import com.sidifensen.domain.dto.ColumnFilterDto;
import com.sidifensen.domain.dto.ColumnSearchDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.ColumnDetailVo;
import com.sidifensen.domain.vo.ColumnVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.UserColumnManageVo;
import com.sidifensen.service.ColumnService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-08-26
 */
@RestController
@RequestMapping("/column")
public class ColumnController {

    @Resource
    private ColumnService columnService;

    /**
     * 获取用户专栏列表(新增文章)
     *
     * @return 专栏列表
     */
    @GetMapping("/list")
    public Result<List<ColumnVo>> getColumnList() {
        List<ColumnVo> columnList = columnService.getColumnList();
        return Result.success(columnList);
    }

    /**
     * 根据用户ID获取专栏列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 专栏列表
     */
    @GetMapping("/list/{userId}")
    public Result<PageVo<List<ColumnVo>>> getColumnListByUserId(@PathVariable Integer userId,
                                                                @NotNull Integer pageNum,
                                                                @NotNull Integer pageSize) {
        PageVo<List<ColumnVo>> columnList = columnService.getColumnListByUserId(userId, pageNum, pageSize);
        return Result.success(columnList);
    }


    /**
     * 获取专栏详情（包含专栏内的文章列表）
     *
     * @param columnId 专栏ID
     * @return 专栏详情
     */
    @GetMapping("/detail/{columnId}")
    public Result<ColumnDetailVo> getColumnDetail(@PathVariable Integer columnId) {
        ColumnDetailVo columnDetail = columnService.getColumnDetail(columnId);
        return Result.success(columnDetail);
    }


    /**
     * 搜索专栏列表
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param keyword  搜索关键词
     * @return 专栏列表
     */
    @GetMapping("/search")
    public Result<PageVo<List<ColumnVo>>> searchColumnList(@NotNull Integer pageNum,
                                                           @NotNull Integer pageSize,
                                                           String keyword) {
        PageVo<List<ColumnVo>> columnList = columnService.searchColumnList(pageNum, pageSize, keyword);
        return Result.success(columnList);
    }

    /**
     * 获取用户专栏列表(专栏管理)
     *
     * @param pageNum         页码
     * @param pageSize        页大小
     * @param columnFilterDto 专栏筛选条件
     * @return 用户专栏列表
     */
    @PostMapping("/manage/list")
    public Result<PageVo<List<UserColumnManageVo>>> getUserColumnManageList(@NotNull Integer pageNum, @NotNull Integer pageSize,
                                                                            @RequestBody ColumnFilterDto columnFilterDto) {
        PageVo<List<UserColumnManageVo>> columnList = columnService.getUserColumnManageList(pageNum, pageSize, columnFilterDto);
        return Result.success(columnList);
    }

    /**
     * 增加专栏
     *
     * @param columnDto 专栏信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Void> addColumn(@RequestBody @Valid ColumnDto columnDto) {
        columnService.addColumn(columnDto);
        return Result.success();
    }

    /**
     * 修改专栏
     *
     * @param columnDto 专栏信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<Void> updateColumn(@RequestBody @Valid ColumnDto columnDto) {
        columnService.updateColumn(columnDto);
        return Result.success();
    }

    /**
     * 删除专栏
     *
     * @param id 专栏ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteColumn(@PathVariable Integer id) {
        columnService.deleteColumn(id);
        return Result.success();
    }

    /**
     * 调整专栏内文章排序
     *
     * @param columnId 专栏ID
     * @param sortList 文章排序列表
     * @return 操作结果
     */
    @PutMapping("/{columnId}/article/sort")
    public Result<Void> updateColumnArticleSort(@PathVariable Integer columnId,
                                                @RequestBody @Valid List<ColumnArticleSortDto> sortList) {
        columnService.updateColumnArticleSort(columnId, sortList);
        return Result.success();
    }

    /**
     * 从专栏中删除文章
     *
     * @param columnId  专栏ID
     * @param articleId 文章ID
     * @return 操作结果
     */
    @DeleteMapping("/{columnId}/article/{articleId}")
    public Result<Void> removeArticleFromColumn(@PathVariable Integer columnId,
                                                @PathVariable Integer articleId) {
        columnService.removeArticleFromColumn(columnId, articleId);
        return Result.success();
    }

    /**
     * 管理员获取专栏列表
     *
     * @param columnFilterDto 专栏筛选条件
     * @return 专栏列表
     */
    @PreAuthorize("hasAuthority('column:list')")
    @PostMapping("/admin/list")
    public Result<List<UserColumnManageVo>> adminGetColumnList(@RequestBody ColumnFilterDto columnFilterDto) {
        List<UserColumnManageVo> columnList = columnService.adminGetColumnList(columnFilterDto);
        return Result.success(columnList);
    }

    /**
     * 管理员根据用户ID获取专栏列表
     *
     * @param userId 用户ID
     * @return 用户专栏列表
     */
    @PreAuthorize("hasAuthority('column:user:list')")
    @GetMapping("/admin/user/{userId}")
    public Result<List<UserColumnManageVo>> adminGetColumnsByUserId(@PathVariable Integer userId) {
        List<UserColumnManageVo> columnList = columnService.adminGetColumnsByUserId(userId);
        return Result.success(columnList);
    }

    /**
     * 管理员搜索专栏
     *
     * @param columnSearchDto 专栏搜索条件
     * @return 搜索结果
     */
    @PreAuthorize("hasAuthority('column:search')")
    @PostMapping("/admin/search")
    public Result<List<UserColumnManageVo>> adminSearchColumn(@RequestBody ColumnSearchDto columnSearchDto) {
        List<UserColumnManageVo> columnList = columnService.adminSearchColumn(columnSearchDto);
        return Result.success(columnList);
    }

    /**
     * 管理员审核专栏
     *
     * @param columnId      专栏ID
     * @param examineStatus 审核状态 1-审核通过 2-审核未通过
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('column:examine')")
    @PutMapping("/admin/{columnId}/examine")
    public Result<Void> adminExamineColumn(@PathVariable Integer columnId, @RequestParam Integer examineStatus) {
        columnService.adminExamineColumn(columnId, examineStatus);
        return Result.success();
    }

    /**
     * 管理员批量审核专栏
     *
     * @param columnIds     专栏ID列表
     * @param examineStatus 审核状态 1-审核通过 2-审核未通过
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('column:examine')")
    @PutMapping("/admin/batch/examine")
    public Result<Void> adminBatchExamineColumn(@RequestBody List<Integer> columnIds, @RequestParam Integer examineStatus) {
        columnService.adminBatchExamineColumn(columnIds, examineStatus);
        return Result.success();
    }

    /**
     * 管理员删除专栏
     *
     * @param columnId 专栏ID
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('column:delete')")
    @DeleteMapping("/admin/{columnId}")
    public Result<Void> adminDeleteColumn(@PathVariable Integer columnId) {
        columnService.adminDeleteColumn(columnId);
        return Result.success();
    }

    /**
     * 管理员批量删除专栏
     *
     * @param columnIds 专栏ID列表
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('column:delete')")
    @DeleteMapping("/admin/batch")
    public Result<Void> adminBatchDeleteColumn(@RequestBody List<Integer> columnIds) {
        columnService.adminBatchDeleteColumn(columnIds);
        return Result.success();
    }
}