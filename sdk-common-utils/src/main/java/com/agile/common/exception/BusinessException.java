package com.agile.common.exception;

import com.agile.common.enums.ErrorCode;

/**
 * 业务异常（业务逻辑错误，如余额不足、状态冲突等）
 */
public class BusinessException extends AppException {
    public BusinessException(String code, String message) {
        super(code, message);
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDefaultMessage());
    }

    public BusinessException(ErrorCode errorCode, Object[] args) {
        super(errorCode.getCode(), errorCode.getDefaultMessage(), args);
    }

    public BusinessException(ErrorCode errorCode, Object[] args, Throwable cause) {
        super(errorCode.getCode(), errorCode.getDefaultMessage(), args, cause);
    }
}