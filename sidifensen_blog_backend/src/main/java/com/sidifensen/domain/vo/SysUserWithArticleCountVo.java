package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息包含文章数量VO
 * 
 * @author sidifensen
 * @since 2024-01-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserWithArticleCountVo extends SysUserVo {

  /**
   * 文章数量
   */
  private Integer articleCount;

}
