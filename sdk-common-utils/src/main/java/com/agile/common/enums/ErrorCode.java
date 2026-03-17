package com.agile.common.enums;

/**
 * 系统错误码枚举
 * 使用全数字错误码，按区间分类：
 * 1000-1999 系统级错误
 * 2000-2999 参数校验错误
 * 3000-3999 数据错误
 * 4000-4999 权限错误
 * 5000-5999 业务错误
 * 6000-6999 第三方服务错误
 * 7000-7099 加解密模块专用
 */
public enum ErrorCode {

    SUCCESS("0", "成功"),

    // ========== 系统级错误 1000-1999 ==========
    SYSTEM_ERROR("1000", "系统内部错误"),
    SERVICE_UNAVAILABLE("1001", "服务不可用"),
    TIMEOUT("1002", "请求超时"),
    UNSUPPORTED_OPERATION("1003", "不支持的操作"),
    IO_ERROR("1004", "输入输出错误"),

    // ========== 参数校验错误 2000-2999 ==========
    PARAM_INVALID("2000", "无效的参数"),
    PARAM_MISSING("2001", "缺少必要参数"),
    PARAM_TYPE_MISMATCH("2002", "参数类型不匹配"),
    PARAM_FORMAT_ERROR("2003", "参数格式错误"),
    PARAM_OUT_OF_RANGE("2004", "参数超出范围"),

    // ========== 数据错误 3000-3999 ==========
    DATA_NOT_FOUND("3000", "数据不存在"),
    DATA_ALREADY_EXISTS("3001", "数据已存在"),
    DATA_INTEGRITY_VIOLATION("3002", "数据完整性违反"),
    DATA_STATE_ERROR("3003", "数据状态错误"),
    DATA_CONFLICT("3004", "数据冲突"),

    // ========== 权限错误 4000-4999 ==========
    UNAUTHORIZED("4000", "未授权"),
    FORBIDDEN("4001", "权限不足"),
    TOKEN_INVALID("4002", "无效的令牌"),
    TOKEN_EXPIRED("4003", "令牌已过期"),
    ACCESS_DENIED("4004", "访问被拒绝"),

    // ========== 业务错误 5000-5999 ==========
    BIZ_ERROR("5000", "业务错误"),
    BIZ_FLOW_LIMIT("5001", "操作过于频繁"),
    BIZ_DEPENDENCY_FAIL("5002", "依赖服务失败"),
    BIZ_STATE_CONFLICT("5003", "业务状态冲突"),
    BIZ_VALIDATION_FAIL("5004", "业务校验失败"),

    // ========== 第三方服务错误 6000-6999 ==========
    THIRD_SERVICE_ERROR("6000", "第三方服务错误"),
    THIRD_TIMEOUT("6001", "第三方服务超时"),
    THIRD_RESPONSE_ERROR("6002", "第三方响应无效"),
    THIRD_API_ERROR("6003", "第三方接口错误"),

    // ========== 加解密模块专用 7000-7099 ==========
    ENCRYPT_ERROR("7000", "加密失败"),
    DECRYPT_ERROR("7001", "解密失败"),
    KEY_INVALID("7002", "无效的加密密钥"),
    ;

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode ec : values()) {
            if (ec.code.equals(code)) {
                return ec;
            }
        }
        return null;
    }
}