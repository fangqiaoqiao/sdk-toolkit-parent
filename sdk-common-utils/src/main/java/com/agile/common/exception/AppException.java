package com.agile.common.exception;

/**
 * 应用异常基类，所有业务异常继承此类
 */
public class AppException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;          // 错误码
    private final transient Object[] args; // 参数（用于国际化或动态填充消息）

    public AppException(String code, String message) {
        super(message);
        this.code = code;
        this.args = null;
    }

    public AppException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.args = null;
    }

    public AppException(String code, String message, Object[] args) {
        super(message);
        this.code = code;
        this.args = args;
    }

    public AppException(String code, String message, Object[] args, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return String.format("AppException{code='%s', message='%s'}", code, getMessage());
    }
}
