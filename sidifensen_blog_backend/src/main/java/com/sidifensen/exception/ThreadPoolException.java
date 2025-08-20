package com.sidifensen.exception;

/**
 * 线程池相关异常类
 */
public class ThreadPoolException extends RuntimeException {

    /**
     * 构造一个带有指定详细消息的线程池异常
     *
     * @param message 异常的详细信息
     */
    public ThreadPoolException(String message) {
        super(message);
    }

    /**
     * 构造一个带有指定详细消息和原因的线程池异常
     *
     * @param message 异常的详细信息
     * @param cause   异常的原因
     */
    public ThreadPoolException(String message, Throwable cause) {
        super(message, cause);
    }
}