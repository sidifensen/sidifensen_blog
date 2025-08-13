package com.sidifensen.service;

/**
 * @author sidifensen
 * @since 2025-08-13
 */
public interface IpService {

    void setRegisterIp(Integer id, String ip);

    void setLoginIp(Integer id, String ip);
}
