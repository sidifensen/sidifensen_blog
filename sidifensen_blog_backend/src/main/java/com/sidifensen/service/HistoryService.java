package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.History;

/**
 * 浏览历史服务接口
 * 
 * @author sidifensen
 * @since 2025-09-19
 */
public interface HistoryService extends IService<History> {

    /**
     * 检查并记录文章浏览
     * 如果用户/访客未浏览过该文章，则增加阅读量并记录浏览历史
     * 
     * @param articleId 文章ID
     * @param userId    用户ID（可为空，表示未登录用户）
     * @param ipAddress IP地址
     * @return true-成功增加阅读量，false-已浏览过，未增加阅读量
     */
    boolean checkAndRecordRead(Integer articleId, Integer userId, String ipAddress);

}
