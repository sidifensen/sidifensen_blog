package com.sidifensen.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TagDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    private String category;

    /**
     * 标签名称
     */
    private String name;


}
