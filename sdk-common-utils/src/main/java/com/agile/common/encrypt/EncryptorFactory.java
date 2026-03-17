package com.agile.common.encrypt;

import com.agile.common.encrypt.impl.DesEncryptor;
import com.agile.common.encrypt.impl.Sm4Encryptor;
import com.agile.common.enums.Algorithm;
import com.agile.common.enums.ErrorCode;
import com.agile.common.exception.ValidationException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class EncryptorFactory {

    private static final ConcurrentHashMap<String, Function<String, Encryptor>> REGISTRY = new ConcurrentHashMap<>();

    static {
        register(Algorithm.DES, DesEncryptor::new);
        register(Algorithm.SM4_ECB, key -> new Sm4Encryptor(key, "ECB", null));
        register(Algorithm.SM4_CBC, key -> new Sm4Encryptor(key, "CBC", "ed32fb5d34388842"));
    }

    /**
     * 注册加密器（使用枚举）
     */
    public static void register(Algorithm algorithm, Function<String, Encryptor> creator) {
        REGISTRY.put(algorithm.getCode().toUpperCase(), creator);
    }

    /**
     * 获取加密器（使用枚举）
     */
    public static Encryptor getEncryptor(Algorithm algorithm, String key) {
        Function<String, Encryptor> creator = REGISTRY.get(algorithm.getCode().toUpperCase());
        if (creator == null) {
            throw new ValidationException(ErrorCode.PARAM_INVALID, new Object[]{"algorithm: " + algorithm.getCode()});
        }
        return creator.apply(key);
    }

    /**
     * 判断算法是否支持
     */
    public static boolean isSupported(Algorithm algorithm) {
        return REGISTRY.containsKey(algorithm.getCode().toUpperCase());
    }

}
