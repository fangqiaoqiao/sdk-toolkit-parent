package com.agile.common.encrypt;

public interface Encryptor {

    /**
     * 加密字节数组
     *
     * @param data 待加密的原始数据
     * @return 加密后的字节数组；若加密失败，返回原始数据
     */
    byte[] encrypt(String key, byte[] data);

    /**
     * 解密字节数组
     *
     * @param data 待解密的密文数据
     * @return 解密后的字节数组；若解密失败，返回原始数据
     */
    byte[] decrypt(String key, byte[] data);

}
