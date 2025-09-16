package com.sidifensen.domain.result;

/**
 * 图片审核结果类
 */
public class AuditResult {
    /**
     * 审核状态 0 待审核 1 审核通过 2 审核不通过
     */
    private final Integer status;
    /**
     * 错误信息
     */
    private final String errorMessage;

    public AuditResult(Integer status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ImageAuditResult{" +
                "status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}