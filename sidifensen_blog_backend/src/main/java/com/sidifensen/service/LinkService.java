package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.LinkAuditDto;
import com.sidifensen.domain.dto.LinkRequestDto;
import com.sidifensen.domain.dto.LinkSearchDto;
import com.sidifensen.domain.entity.Link;
import com.sidifensen.domain.vo.AdminLinkVo;
import com.sidifensen.domain.vo.LinkVo;
import com.sidifensen.domain.vo.PageVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-28
 */
public interface LinkService extends IService<Link> {

    /**
     * 申请友链
     *
     * @param linkRequestDto 友链申请信息
     */
    void applyLink(LinkRequestDto linkRequestDto);

    /**
     * 申请友链（指定用户ID，用于异步场景）
     *
     * @param linkRequestDto 友链申请信息
     * @param userId         用户ID
     */
    void applyLink(LinkRequestDto linkRequestDto, Integer userId);

    /**
     * 删除友链
     *
     * @param linkId 友链ID
     */
    void deleteLink(Integer linkId);

    /**
     * 分页获取审核通过的友链列表
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @return 分页友链列表
     */
    PageVo<List<LinkVo>> getLinkList(Integer pageNum, Integer pageSize);

    // ==================== 管理员接口 ====================

    /**
     * 管理员获取所有友链列表
     *
     * @return 友链列表
     */
    List<AdminLinkVo> adminGetLinkList();

    /**
     * 管理员搜索友链
     *
     * @param linkSearchDto 搜索条件
     * @return 搜索结果
     */
    List<AdminLinkVo> adminSearchLink(LinkSearchDto linkSearchDto);

    /**
     * 管理员审核友链
     *
     * @param linkAuditDto 友链审核信息
     */
    void adminExamineLink(LinkAuditDto linkAuditDto);

    /**
     * 管理员批量审核友链
     *
     * @param linkAuditDtos 友链审核信息列表
     */
    void adminExamineBatchLink(List<LinkAuditDto> linkAuditDtos);

    /**
     * 管理员删除友链
     *
     * @param linkId 友链ID
     */
    void adminDeleteLink(Integer linkId);

    /**
     * 管理员批量删除友链
     *
     * @param linkIds 友链ID列表
     */
    void adminDeleteBatchLink(List<Integer> linkIds);
}
