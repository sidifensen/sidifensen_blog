package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.Announcement;
import com.sidifensen.domain.vo.AnnouncementVo;
import com.sidifensen.domain.vo.PageVo;

import java.util.List;

/**
 * 公告 Service
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 分页查询公告列表
     *
     * @param status   状态筛选（可选）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageVo<List<AnnouncementVo>> getAnnouncementPage(Integer status, Integer pageNum, Integer pageSize);

    /**
     * 创建公告并提交发送任务
     *
     * @param announcement 公告实体
     */
    void createAnnouncement(Announcement announcement);

    /**
     * 取消待发送公告（仅 status=0 可取消）
     *
     * @param id 公告ID
     */
    void cancelAnnouncement(Integer id);

    /**
     * 删除公告（软删除）
     *
     * @param id 公告ID
     */
    void deleteAnnouncement(Integer id);

    /**
     * 标记公告已读（用户端）
     *
     * @param id        公告ID
     * @param readerId  阅读者用户ID
     */
    void readAnnouncement(Integer id, Integer readerId);

    /**
     * 获取用户已读公告ID列表
     *
     * @param readerId 阅读者用户ID
     * @return 已读公告ID列表
     */
    List<Integer> getReadAnnouncementIds(Integer readerId);
}
