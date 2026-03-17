package com.agile.common.exception;

import com.agile.common.enums.ErrorCode;

/**
 * 系统异常（如网络超时、数据库连接失败、IO错误等）
 */
public class SystemException extends AppException {
    public SystemException(String code, String message) {
        super(code, message);
    }

    public SystemException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public SystemException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDefaultMessage());
    }

    public SystemException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getCode(), errorCode.getDefaultMessage(), cause);
    }

    public SystemException(ErrorCode errorCode,String message, Throwable cause) {
        super(errorCode.getCode(), message, cause);
    }

    public SystemException(ErrorCode errorCode, Object[] args) {
        super(errorCode.getCode(), errorCode.getDefaultMessage(), args);
    }
}
