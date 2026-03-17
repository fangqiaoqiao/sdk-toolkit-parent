package com.agile.common.encrypt.support;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

/**
 * DES 核心算法，仅提供字节数组加解密
 */
public class DesCore {

    private static final String DEFAULT_KEY = "stin.fly";

    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    public DesCore() throws Exception {
        this(DEFAULT_KEY);
    }

    public DesCore(String key) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        byte[] keyBytes = key.getBytes();
        SecretKey secretKey = new SecretKeySpec(buildKey(keyBytes), "DES");

        encryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

        decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
    }

    private byte[] buildKey(byte[] source) {
        byte[] key = new byte[8];
        for (int i = 0; i < source.length && i < key.length; i++) {
            key[i] = source[i];
        }
        return key;
    }

    public byte[] encrypt(byte[] data) throws Exception {
        return encryptCipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws Exception {
        return decryptCipher.doFinal(data);
    }

}
