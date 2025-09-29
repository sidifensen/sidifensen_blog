package com.sidifensen.controller;

import com.sidifensen.domain.dto.LinkAuditDto;
import com.sidifensen.domain.dto.LinkRequestDto;
import com.sidifensen.domain.dto.LinkSearchDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.AdminLinkVo;
import com.sidifensen.domain.vo.LinkVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.service.LinkService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-09-28
 */
@RestController
@RequestMapping("/link")
public class LinkController {

    @Resource
    private LinkService linkService;

    /**
     * 申请友链
     *
     * @param linkRequestDto 友链申请信息
     * @return 操作结果
     */
    @PostMapping("/apply")
    public Result<Void> applyLink(@Valid @RequestBody LinkRequestDto linkRequestDto) {
        linkService.applyLink(linkRequestDto);
        return Result.success(null);
    }

    /**
     * 删除友链
     *
     * @param linkId 友链ID
     * @return 操作结果
     */
    @DeleteMapping("/{linkId}")
    public Result<Void> deleteLink(@PathVariable @NotNull Integer linkId) {
        linkService.deleteLink(linkId);
        return Result.success(null);
    }

    /**
     * 分页获取审核通过的友链列表
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @return 分页友链列表
     */
    @GetMapping("/list")
    public Result<PageVo<List<LinkVo>>> getLinkList(@RequestParam @NotNull Integer pageNum, @RequestParam @NotNull Integer pageSize) {
        PageVo<List<LinkVo>> linkList = linkService.getLinkList(pageNum, pageSize);
        return Result.success(linkList);
    }

    /**
     * 管理员获取所有友链列表
     *
     * @return 友链列表
     */
    @PreAuthorize("hasAuthority('link:list')")
    @GetMapping("/admin/list")
    public Result<List<AdminLinkVo>> adminGetLinkList() {
        List<AdminLinkVo> linkList = linkService.adminGetLinkList();
        return Result.success(linkList);
    }

    /**
     * 管理员搜索友链
     *
     * @param linkSearchDto 搜索条件
     * @return 搜索结果
     */
    @PreAuthorize("hasAuthority('link:search')")
    @PostMapping("/admin/search")
    public Result<List<AdminLinkVo>> adminSearchLink(@RequestBody LinkSearchDto linkSearchDto) {
        List<AdminLinkVo> linkList = linkService.adminSearchLink(linkSearchDto);
        return Result.success(linkList);
    }

    /**
     * 管理员审核友链
     *
     * @param linkAuditDto 友链审核信息
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('link:examine')")
    @PutMapping("/admin/examine")
    public Result<Void> adminExamineLink(@RequestBody LinkAuditDto linkAuditDto) {
        linkService.adminExamineLink(linkAuditDto);
        return Result.success();
    }

    /**
     * 管理员批量审核友链
     *
     * @param linkAuditDtos 友链审核信息列表
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('link:examine')")
    @PutMapping("/admin/examine/batch")
    public Result<Void> adminExamineBatchLink(@RequestBody List<LinkAuditDto> linkAuditDtos) {
        linkService.adminExamineBatchLink(linkAuditDtos);
        return Result.success();
    }

    /**
     * 管理员删除友链
     *
     * @param linkId 友链ID
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('link:delete')")
    @DeleteMapping("/admin/{linkId}")
    public Result<Void> adminDeleteLink(@PathVariable Integer linkId) {
        linkService.adminDeleteLink(linkId);
        return Result.success();
    }

    /**
     * 管理员批量删除友链
     *
     * @param linkIds 友链ID列表
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('link:delete')")
    @DeleteMapping("/admin/delete/batch")
    public Result<Void> adminDeleteBatchLink(@RequestBody List<Integer> linkIds) {
        linkService.adminDeleteBatchLink(linkIds);
        return Result.success();
    }
}
