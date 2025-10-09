package com.sidifensen.exception;

import com.sidifensen.domain.entity.LoginUser;
import com.sidifensen.domain.result.Result;
import com.sidifensen.utils.SecurityUtils;
import com.sidifensen.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLSyntaxErrorException;

/**
 * 全局异常处理
 */
@RestControllerAdvice // 如果用@ControllerAdvice，则需要在方法上添加@ResponseBody
@Slf4j
public class GlobalException {

    @ExceptionHandler(NoHandlerFoundException.class)
    Object handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("请求地址出错: {} ", e.getMessage());
        return Result.error("请求地址出错");
    }

    @ExceptionHandler(Exception.class)
    Object handleException(Exception e) {
        e.printStackTrace();
        log.error("系统异常：{}", e.getMessage());
        return Result.error("系统异常，请联系管理员");
    }

    @ExceptionHandler(BlogException.class)
    Object handleBlogException(BlogException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(RateLimitException.class)
    Object handleRateLimitException(RateLimitException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    Object handleAuthenticationException(AuthenticationException e) {
//        log.error("认证异常：{}", e.getMessage()); // 用户帐号已被锁定
        return Result.unauthorized(e.getMessage()); // BadCredentialsException: 用户名或密码错误
    }

    @ExceptionHandler(AccessDeniedException.class)
    Object handleAccessDeniedException(AccessDeniedException e) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            // 获取访问的url
            String requestUrl = WebUtils.getRequestUrl();
            log.error("userId:{} 权限异常：{}; 访问url:{} ; 权限: {}",
                    loginUser.getSysUser().getId(), e.getMessage(), requestUrl, loginUser.getAuthorities());
        }
        return Result.unauthorized("无权限"); // AccessDeniedException: 无权限
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常:{}({})", e.getMessage(), e.getStackTrace());
        BindingResult bindingResult = e.getBindingResult();// 获取参数绑定结果
        String errorMsg = bindingResult.getFieldError().getDefaultMessage(); // 获取参数校验错误信息
        e.printStackTrace();
        return Result.error(errorMsg == null ? "参数校验异常" : errorMsg);
    }

    @ExceptionHandler(FileUploadException.class)
    Object handlerFileUploadException(FileUploadException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    Object handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("文件上传大小超出系统限制：{}({})", e.getMessage(), e.getStackTrace());
        return Result.error("文件上传大小超出系统限制");
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    Object handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error("SQL语法错误：{}({})", e.getMessage(), e.getStackTrace());
        return Result.error("SQL语法错误");
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    Object handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("数据库语法错误：{}", e.getMessage());
        e.printStackTrace();
        return Result.error("数据库查询错误，请检查参数是否正确");
    }

    @ExceptionHandler(ThreadPoolException.class)
    Object handleThreadPoolException(ThreadPoolException e) {
        log.error("线程池异常：{}", e.getMessage());
        e.printStackTrace();
        return Result.error(e.getMessage());
    }

    /**
     * 处理客户端主动断开连接的异常
     * 这种情况通常是客户端重启或网络问题导致，不需要特别处理
     */
    @ExceptionHandler(ClientAbortException.class)
    void handleClientAbortException(ClientAbortException e) {

    }

    /**
     * 处理文件上传过程中客户端断开连接的异常
     * 这种情况通常是客户端重启或网络问题导致，不需要特别处理
     */
    @ExceptionHandler(MultipartException.class)
    Object handleMultipartException(MultipartException e) {
        log.error("文件上传过程中客户端断开连接: {}", e.getMessage());
        // 可以选择返回一个友好的错误信息或者空响应
        return Result.error("文件上传中断，请重新上传");
    }

    /**
     * 处理查询超时异常
     */
    @ExceptionHandler(QueryTimeoutException.class)
    Object handleQueryTimeoutException(QueryTimeoutException e) {
        log.error("服务器超时：{}", e.getMessage());
        e.printStackTrace();
        return Result.error("服务器超时");
    }

}