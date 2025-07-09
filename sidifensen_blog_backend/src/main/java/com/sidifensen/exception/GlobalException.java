package com.sidifensen.exception;

import com.sidifensen.domain.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * 全局异常处理
 */
@RestControllerAdvice // 如果用@ControllerAdvice，则需要在方法上添加@ResponseBody
@Slf4j
public class GlobalException {


    @ExceptionHandler(Exception.class)
    Object handleException(Exception e) {
        e.printStackTrace();
        log.error("系统异常：" + e.getMessage());
        return Result.error("系统异常，请联系管理员");
    }

    @ExceptionHandler(BlogException.class)
    Object handleBlogException(BlogException e) {
        log.error("博客业务异常：" + e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    Object handleAuthenticationException(AuthenticationException e) {
        log.error("认证异常："+ e.getMessage());
        return Result.unauthorized(e.getMessage()); // BadCredentialsException: 用户名或密码错误
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常：" + e.getMessage());
        BindingResult bindingResult = e.getBindingResult();// 获取参数绑定结果
        String errorMsg = bindingResult.getFieldError().getDefaultMessage(); // 获取参数校验错误信息
        return Result.error(errorMsg == null? "参数校验异常" : errorMsg);
    }
}
