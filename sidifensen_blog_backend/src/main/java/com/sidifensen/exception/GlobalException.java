package com.sidifensen.exception;

import com.sidifensen.domain.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
        log.error("参数校验异常:{}({})", e.getMessage(), e.getStackTrace());
        BindingResult bindingResult = e.getBindingResult();// 获取参数绑定结果
        String errorMsg = bindingResult.getFieldError().getDefaultMessage(); // 获取参数校验错误信息
        return Result.error(errorMsg == null? "参数校验异常" : errorMsg);
    }

    @ExceptionHandler(FileUploadException.class)
    Object handlerFileUploadException(FileUploadException e){
        log.error("文件上传异常:{}({})", e.getMessage(), e.getStackTrace());
        return Result.success(e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    Object handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("文件上传大小超出系统限制：{}({})", e.getMessage(), e.getStackTrace());
        return Result.error("文件上传大小超出系统限制");
    }

}
