package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息包含专栏数量VO
 * 
 * @author sidifensen
 * @since 2025-09-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserWithColumnCountVo extends SysUserVo {

  /**
   * 专栏数量
   */
  private Integer columnCount;

}
