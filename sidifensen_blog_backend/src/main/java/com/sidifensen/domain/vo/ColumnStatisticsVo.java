package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 专栏统计数据视图对象
 * 
 * @author sidifensen
 * @since 2025-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ColumnStatisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 专栏总数
     */
    private Integer totalColumns;

    /**
     * 待审核专栏数
     */
    private Integer pendingColumns;

    /**
     * 已审核专栏数
     */
    private Integer approvedColumns;

    /**
     * 审核未通过专栏数
     */
    private Integer rejectedColumns;

    /**
     * 公开专栏数
     */
    private Integer publicColumns;

    /**
     * 私密专栏数
     */
    private Integer privateColumns;

    /**
     * 本月新增专栏数
     */
    private Integer monthlyNewColumns;

    /**
     * 今日新增专栏数
     */
    private Integer dailyNewColumns;

}
