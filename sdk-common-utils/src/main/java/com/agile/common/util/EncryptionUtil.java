package com.agile.common.util;

import com.agile.common.encrypt.Encryptor;
import com.agile.common.encrypt.EncryptorFactory;
import com.agile.common.enums.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

/**
 * 加密解密工具类（DES专用）。
 * 提供字节数组与字符串的加解密，完全兼容 {@code DesPlus} 的行为（使用平台默认字符集）。
 */
public final class EncryptionUtil {

    private static final Logger log = LoggerFactory.getLogger(EncryptionUtil.class);

    private EncryptionUtil() {
        // 工具类禁止实例化
    }

    public static byte[] encryptBase64(Algorithm algorithm, String key, byte[] out) {
        // 1. 对输出数据 out 进行 Base64 编码（逆步骤5）
        String decryptedStr = Base64.getEncoder().encodeToString(out);

        // 2. 将字符串转回字节数组（逆步骤4，必须使用与 decryptBase64 相同的字符集）
        //    假设 decryptBase64 中使用了平台默认字符集，这里也使用默认字符集
        byte[] decryptedBytes = decryptedStr.getBytes();   // 若环境不同，应显式指定相同字符集，如 StandardCharsets.ISO_8859_1

        // 3. 对字节数组进行加密（逆步骤3）
        byte[] hexBytes = encrypt(algorithm, key, decryptedBytes);

        // 4. 将加密结果转为十六进制字符串（逆步骤2）
        String hexStr = HexUtil.byteArrToHexStr(hexBytes);

        // 5. 对十六进制字符串进行 Base64 解码（逆步骤1）
        return Base64.getDecoder().decode(hexStr);
    }

    public static byte[] decryptBase64(Algorithm algorithm, String key, byte[] data) {
        // 1. Base64 编码（使用标准 Base64，无换行）
        String base64Str = Base64.getEncoder().encodeToString(data);

        // 2. 将 Base64 字符串当作十六进制字符串解析（hexStrToByteArr）
        byte[] hexBytes = HexUtil.hexStrToByteArr(base64Str);

        // 3. 解密（使用与方式一完全相同的 decrypt 方法）
        byte[] decryptedBytes = decrypt(algorithm, key, hexBytes);

        // 4. 转成字符串
        String decryptedStr = new String(decryptedBytes);

        // 5. 再次 Base64 解码
        return Base64.getDecoder().decode(decryptedStr);
    }

    public static byte[] encrypt(Algorithm algorithm, String key, byte[] data) {
        Encryptor encryptor = EncryptorFactory.getEncryptor(algorithm);
        return encryptor.encrypt(key, data);
    }

    public static byte[] decrypt(Algorithm algorithm, String key, byte[] data) {
        Encryptor encryptor = EncryptorFactory.getEncryptor(algorithm);
        return encryptor.decrypt(key, data);
    }

}