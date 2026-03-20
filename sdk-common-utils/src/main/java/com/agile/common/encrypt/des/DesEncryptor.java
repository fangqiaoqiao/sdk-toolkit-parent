package com.agile.common.encrypt.des;

import com.agile.common.encrypt.Encryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * DES 核心算法，仅提供字节数组加解密
 */

public class DesEncryptor implements Encryptor {

    private static final Logger log = LoggerFactory.getLogger(DesEncryptor.class);

    public DesEncryptor() {
    }

    public byte[] encrypt(String key, byte[] data) {
        return execute(Cipher.ENCRYPT_MODE, key, data);
    }

    public byte[] decrypt(String key, byte[] data) {
        return execute(Cipher.DECRYPT_MODE, key, data);
    }

    private byte[] execute(int mode, String keyStr, byte[] data) {
        try {
            byte[] keyBytes = buildKey(keyStr);
            Key key = getKey(keyBytes);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(mode, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("加密异常", e);
        }
        return data;
    }

    private Key getKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "DES");
    }

    private byte[] buildKey(String key) {
        byte[] source = key.getBytes();
        byte[] bytes = new byte[8];
        for (int i = 0; i < source.length && i < bytes.length; i++) {
            bytes[i] = source[i];
        }
        return bytes;
    }
}