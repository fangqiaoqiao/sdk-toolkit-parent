package com.agile.common.enums;

public enum Algorithm {

    DES("DES"),
    SM4_ECB("SM4/ECB"),
    SM4_CBC("SM4/CBC");

    private final String code;

    Algorithm(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * 根据字符串获取枚举（忽略大小写）
     */
    public static Algorithm fromCode(String code) {
        for (Algorithm algo : values()) {
            if (algo.code.equalsIgnoreCase(code)) {
                return algo;
            }
        }
        return null;
    }

}
