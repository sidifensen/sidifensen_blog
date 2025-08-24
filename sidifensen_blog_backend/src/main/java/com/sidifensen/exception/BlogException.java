package com.sidifensen.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BlogException extends RuntimeException{

    // 自定义异常
    public BlogException(String message){
        // 传递异常信息
        super(message);
    }
}