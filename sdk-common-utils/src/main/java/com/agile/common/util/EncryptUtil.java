package com.agile.common.util;

import com.agile.common.enums.Algorithm;
import com.agile.common.encrypt.Encryptor;
import com.agile.common.encrypt.impl.DesEncryptor;
import com.agile.common.encrypt.impl.Sm4Encryptor;
import com.agile.common.enums.ErrorCode;
import com.agile.common.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密解密工具类，提供简单易用的静态方法。
 * 支持算法：DES、SM4/ECB、SM4/CBC。
 * 异常时返回原始数据（字节数组或字符串），可通过日志查看错误详情。
 */
public class EncryptUtil {

    private static final Logger log = LoggerFactory.getLogger(EncryptUtil.class);

    private EncryptUtil() {
        // 工具类禁止实例化
    }

    // -------------------------------------------------------------------------
    // 字节数组加解密
    // -------------------------------------------------------------------------

    /**
     * 加密字节数组。
     *
     * @param data      待加密的原始数据
     * @param algorithm 算法（Algorithm.DES, Algorithm.SM4_ECB, Algorithm.SM4_CBC）
     * @param key       密钥字符串（DES 取前8字节，SM4 需16字节，不足自动补0）
     * @param iv        初始向量（仅 SM4/CBC 需要，16字节十六进制字符串，如 "ed32fb5d34388842"）
     * @return 加密后的字节数组，异常时返回原 data
     */
    public static byte[] encrypt(byte[] data, Algorithm algorithm, String key, String iv) {
        if (data == null) {
            return null;
        }
        try {
            Encryptor encryptor = createEncryptor(algorithm, key, iv);
            return encryptor.encrypt(data);
        } catch (Exception e) {
            log.error("加密失败: {}", ExceptionUtil.getExceptionStr(e));
            return data;
        }
    }

    /**
     * 解密字节数组。
     *
     * @param data      待解密的密文数据
     * @param algorithm 算法
     * @param key       密钥字符串
     * @param iv        初始向量（仅 SM4/CBC 需要）
     * @return 解密后的字节数组，异常时返回原 data
     */
    public static byte[] decrypt(byte[] data, Algorithm algorithm, String key, String iv) {
        if (data == null) {
            return null;
        }
        try {
            Encryptor encryptor = createEncryptor(algorithm, key, iv);
            return encryptor.decrypt(data);
        } catch (Exception e) {
            log.error("解密失败: {}", ExceptionUtil.getExceptionStr(e));
            return data;
        }
    }

    // -------------------------------------------------------------------------
    // Base64 字符串加解密（UTF-8 编码）
    // -------------------------------------------------------------------------

    /**
     * 加密字符串，返回 Base64 编码结果。
     *
     * @param data      待加密的原始字符串（UTF-8）
     * @param algorithm 算法
     * @param key       密钥
     * @param iv        初始向量（仅 SM4/CBC 需要）
     * @return Base64 密文字符串，异常时返回原 data
     */
    public static String encryptToBase64(String data, Algorithm algorithm, String key, String iv) {
        if (data == null) {
            return null;
        }
        byte[] encrypted = encrypt(data.getBytes(StandardCharsets.UTF_8), algorithm, key, iv);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密 Base64 字符串，返回原始字符串（UTF-8）。
     *
     * @param base64Data Base64 密文
     * @param algorithm  算法
     * @param key        密钥
     * @param iv         初始向量（仅 SM4/CBC 需要）
     * @return 原始字符串，异常时返回原 base64Data
     */
    public static String decryptFromBase64(String base64Data, Algorithm algorithm, String key, String iv) {
        if (base64Data == null) {
            return null;
        }
        try {
            byte[] data = Base64.getDecoder().decode(base64Data);
            byte[] decrypted = decrypt(data, algorithm, key, iv);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            log.error("Base64 解码失败: {}", ExceptionUtil.getExceptionStr(e));
            return base64Data;
        }
    }

    // -------------------------------------------------------------------------
    // 私有辅助方法
    // -------------------------------------------------------------------------

    private static Encryptor createEncryptor(Algorithm algorithm, String key, String iv) {
        switch (algorithm) {
            case DES:
                return new DesEncryptor(key);
            case SM4_ECB:
                return new Sm4Encryptor(key, "ECB", null);
            case SM4_CBC:
                if (iv == null) {
                    throw new ValidationException(ErrorCode.PARAM_MISSING.getCode(), "SM4/CBC 模式必须提供 iv 参数");
                }
                return new Sm4Encryptor(key, "CBC", iv);
            default:
                throw new ValidationException(ErrorCode.PARAM_INVALID.getCode(), "不支持的算法: " + algorithm);
        }
    }
}