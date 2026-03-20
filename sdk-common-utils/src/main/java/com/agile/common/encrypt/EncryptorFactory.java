package com.agile.common.encrypt;

import com.agile.common.encrypt.des.DesEncryptor;
import com.agile.common.enums.Algorithm;
import com.agile.common.enums.ErrorCode;
import com.agile.common.exception.ValidationException;

import java.util.concurrent.ConcurrentHashMap;

public class EncryptorFactory {

    private static final ConcurrentHashMap<String, Encryptor> REGISTRY = new ConcurrentHashMap<>();

    static {
        // 注册无状态单例
        register(Algorithm.DES, new DesEncryptor());
    }

    private static void register(Algorithm algorithm, Encryptor encryptor) {
        if (algorithm == null || encryptor == null) {
            throw new ValidationException(ErrorCode.PARAM_INVALID, "算法或加密器不能为空");
        }
        REGISTRY.put(algorithm.getCode().toUpperCase(), encryptor);
    }

    public static Encryptor getEncryptor(Algorithm algorithm) {
        Encryptor encryptor = REGISTRY.get(algorithm.getCode().toUpperCase());
        if (encryptor == null) {
            throw new ValidationException(ErrorCode.PARAM_INVALID, "暂不支持加解密类型：" + algorithm.getCode());
        }
        return encryptor;
    }

    public static boolean isSupported(Algorithm algorithm) {
        return algorithm != null && REGISTRY.containsKey(algorithm.getCode().toUpperCase());
    }
}