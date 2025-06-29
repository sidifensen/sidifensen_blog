package com.sidifensen.exception;

import com.sidifensen.domain.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalException {


    @ExceptionHandler(Exception.class)
    Object handleException(Exception e) {
        e.printStackTrace();
        return Result.error("系统异常，请联系管理员");
    }

    @ExceptionHandler(BlogException.class)
    Object handleBlogException(BlogException e) {
        return Result.error(e.getMessage());
    }


}
