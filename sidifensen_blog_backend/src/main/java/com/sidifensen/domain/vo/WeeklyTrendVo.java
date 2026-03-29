package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serial;
import java.io.Serializable;

/**
 * 周趋势单日数据 VO
 *
 * @author sidifensen
 * @since 2026-03-28
 */
@Data
@Accessors(chain = true)
public class WeeklyTrendVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 日期，格式：2026-03-22 */
    private String date;

    /** 当日新增文章数 */
    private Integer articleCount;

    /** 当日新增用户数 */
    private Integer userCount;
}