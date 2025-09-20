package com.sidifensen.controller;


import com.sidifensen.domain.dto.ColumnDto;
import com.sidifensen.domain.dto.ColumnFilterDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.ColumnVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.UserColumnManageVo;
import com.sidifensen.service.ColumnService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
     * 获取用户专栏列表
     *
     * @return 专栏列表
     */
    @GetMapping("/list")
    public Result<List<ColumnVo>> getColumnList() {
        List<ColumnVo> columnList = columnService.getColumnList();
        return Result.success(columnList);
    }

    /**
     * 获取用户专栏列表(专栏管理)
     *
     * @param pageNum          页码
     * @param pageSize         页大小
     * @param columnFilterDto  专栏筛选条件
     * @return 用户专栏列表
     */
    @PostMapping("/manage/list")
    public Result<PageVo<List<UserColumnManageVo>>> getUserColumnManageList(@NotNull Integer pageNum, @NotNull Integer pageSize,
                                                                            @RequestBody ColumnFilterDto columnFilterDto) {
        PageVo<List<UserColumnManageVo>> columnList = columnService.getUserColumnManageList(pageNum, pageSize, columnFilterDto);
        return Result.success(columnList);
    }

    /**
     * 获取专栏详情
     *
     * @param id 专栏ID
     * @return 专栏详情
     */
//    @GetMapping("/{id}")
//    public Result<ColumnVo> getColumn(@PathVariable Integer id) {
//        ColumnVo columnVo = columnService.getColumn(id);
//        return Result.success(columnVo);
//    }

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
}