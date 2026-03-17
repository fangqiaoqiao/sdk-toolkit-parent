package com.agile.common.exception;

import com.agile.common.enums.ErrorCode;

import java.util.List;

/**
 * 参数校验异常（参数格式错误、缺失、范围越界等）
 */
public class ValidationException extends AppException {
    // 可选：携带字段级错误详情
    private final transient List<FieldError> fieldErrors;

    public ValidationException(String code, String message) {
        super(code, message);
        this.fieldErrors = null;
    }
    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
        this.fieldErrors = null;
    }
    public ValidationException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDefaultMessage());
        this.fieldErrors = null;
    }

    public ValidationException(ErrorCode errorCode, Object[] args) {
        super(errorCode.getCode(), errorCode.getDefaultMessage(), args);
        this.fieldErrors = null;
    }

    public ValidationException(ErrorCode errorCode, List<FieldError> fieldErrors) {
        super(errorCode.getCode(), errorCode.getDefaultMessage());
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * 内部类：字段错误详情
     */
    public static class FieldError {
        private final String field;
        private final String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        // getters...
        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}