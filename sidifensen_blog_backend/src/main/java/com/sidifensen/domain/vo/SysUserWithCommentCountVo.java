package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息包含评论数量VO
 * 
 * @author sidifensen
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserWithCommentCountVo extends SysUserVo {

  /**
   * 评论数量
   */
  private Integer commentCount;

}
