package com.sidifensen.controller;

import com.sidifensen.aspect.OperationLog;
import com.sidifensen.domain.entity.Announcement;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.AnnouncementVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.service.AnnouncementService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告管理 Controller
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    /**
     * 分页查询公告列表（管理端）
     *
     * @param status   状态筛选（可选）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return
     */
    @OperationLog(module = "公告管理", type = OperationTypeEnum.SELECT, description = "分页查询公告列表")
    @PreAuthorize("hasAuthority('announcement:list')")
    @GetMapping("/admin/page")
    public Result getAnnouncementPage(
            @RequestParam(required = false) Integer status,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        PageVo<List<AnnouncementVo>> page = announcementService.getAnnouncementPage(status, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 创建公告
     *
     * @param announcement 公告信息
     * @return
     */
    @OperationLog(module = "公告管理", type = OperationTypeEnum.INSERT, description = "创建公告")
    @PreAuthorize("hasAuthority('announcement:create')")
    @PostMapping
    public Result createAnnouncement(@RequestBody @Validated Announcement announcement) {
        announcementService.createAnnouncement(announcement);
        return Result.success();
    }

    /**
     * 取消待发送公告（仅 status=0 可取消）
     *
     * @param id 公告ID
     * @return
     */
    @OperationLog(module = "公告管理", operation = "取消", type = OperationTypeEnum.OTHER, description = "取消待发送公告")
    @PreAuthorize("hasAuthority('announcement:cancel')")
    @PutMapping("/{id}/cancel")
    public Result cancelAnnouncement(@PathVariable Integer id) {
        announcementService.cancelAnnouncement(id);
        return Result.success();
    }

    /**
     * 删除公告（软删除）
     *
     * @param id 公告ID
     * @return
     */
    @OperationLog(module = "公告管理", type = OperationTypeEnum.DELETE, description = "删除公告")
    @PreAuthorize("hasAuthority('announcement:delete')")
    @DeleteMapping("/{id}")
    public Result deleteAnnouncement(@PathVariable Integer id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }

    /**
     * 分页获取公告列表（用户端）
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("/page")
    public Result getUserAnnouncementPage(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        // 用户端只看 website 方式的公告（status=2）
        PageVo<List<AnnouncementVo>> page = announcementService.getAnnouncementPage(2, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 标记公告已读（用户端）
     *
     * @param id 公告ID
     * @return
     */
    @PutMapping("/{id}/read")
    public Result readAnnouncement(@PathVariable Integer id) {
        Integer readerId = SecurityUtils.getUserId();
        announcementService.readAnnouncement(id, readerId);
        return Result.success();
    }

    /**
     * 获取用户已读公告ID列表（用户端）
     *
     * @return
     */
    @GetMapping("/read-ids")
    public Result getReadAnnouncementIds() {
        Integer readerId = SecurityUtils.getUserId();
        List<Integer> readIds = announcementService.getReadAnnouncementIds(readerId);
        return Result.success(readIds);
    }
}
