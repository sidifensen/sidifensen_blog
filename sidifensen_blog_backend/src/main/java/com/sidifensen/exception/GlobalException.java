package com.sidifensen.exception;

import com.sidifensen.domain.result.Result;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalException {


    @ExceptionHandler(Exception.class)
    @ResponseBody
    Object handleException(Exception e) {
        e.printStackTrace();
        return Result.error("系统异常，请联系管理员");
    }

    @ExceptionHandler(BlogException.class)
    @ResponseBody
    Object handleBlogException(BlogException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseBody
    Object handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        return Result.error(e.getMessage());//用户不存在
    }


    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    Object handleBadCredentialsException(BadCredentialsException e) {
        return Result.error(e.getMessage());
    }

}
