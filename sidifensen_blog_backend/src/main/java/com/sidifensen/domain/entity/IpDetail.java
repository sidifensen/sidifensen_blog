package com.sidifensen.domain.entity;

import lombok.Data;

/**
 * @author sidifensen
 * @since 2025-08-11
 */
@Data
public class IpDetail {

    // ip
    private String ip;

    // 国家
    private String country;

    // 省
    private String prov;

    // 市
    private String city;

    // 区
    private String area;

    // 营运商
    private String isp;

}
