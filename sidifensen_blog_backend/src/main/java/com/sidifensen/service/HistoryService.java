package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.History;
import com.sidifensen.domain.vo.HistoryVo;
import com.sidifensen.domain.vo.PageVo;

import java.util.List;

/**
 * 浏览历史服务接口
 * 
 * @author sidifensen
 * @since 2025-09-19
 */
public interface HistoryService extends IService<History> {

    /**
     * 检查并记录文章浏览
     * 登录用户：记录到数据库并缓存到Redis
     * 访客：仅缓存到Redis，用于防重复浏览
     * 
     * @param articleId 文章ID
     * @param userId    用户ID（可为空，表示未登录用户）
     * @param ipAddress IP地址（仅用于生成访客指纹，不存储到数据库）
     * @return true-成功增加阅读量，false-已浏览过，未增加阅读量
     */
    boolean checkAndRecordRead(Integer articleId, Integer userId, String ipAddress);

    /**
     * 分页获取当前用户的文章浏览历史（倒序）
     * 
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页浏览历史数据
     */
    PageVo<List<HistoryVo>> getUserHistoryList(Integer pageNum, Integer pageSize);

    /**
     * 清除当前用户的所有浏览记录
     * 
     * @return 清除的记录数量
     */
    int clearUserHistory();

}
