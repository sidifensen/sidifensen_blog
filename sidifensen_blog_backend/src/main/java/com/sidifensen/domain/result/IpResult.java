package com.sidifensen.domain.result;

import com.sidifensen.domain.entity.IpDetail;
import lombok.Data;

/**
 * @author sidifensen
 * @since 2025-08-11
 */
@Data
public class IpResult {

    private Integer ret;
    private IpDetail data;

}
