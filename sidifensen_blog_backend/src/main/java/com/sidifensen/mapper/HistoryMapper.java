package com.sidifensen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sidifensen.domain.entity.History;
import com.sidifensen.domain.vo.HistoryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-09-19
 */
public interface HistoryMapper extends BaseMapper<History> {

    /**
     * 分页查询用户浏览历史（关联文章和作者信息）
     * 
     * @param page   分页参数
     * @param userId 用户ID
     * @return 浏览历史列表
     */
    List<HistoryVo> getUserHistoryList(Page<HistoryVo> page, @Param("userId") Integer userId);

}