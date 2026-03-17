package com.agile.common.encrypt.impl;

import com.agile.common.encrypt.Encryptor;
import com.agile.common.encrypt.support.HexUtil;
import com.agile.common.encrypt.support.sm4.SM4;
import com.agile.common.encrypt.support.sm4.SM4Context;
import com.agile.common.enums.ErrorCode;
import com.agile.common.exception.SystemException;
import com.agile.common.exception.ValidationException;

import java.nio.charset.StandardCharsets;

public class Sm4Encryptor implements Encryptor {

    private final SM4Context context;
    private final byte[] keyBytes;
    private final byte[] ivBytes;
    private final SM4 sm4 = new SM4();
    private final String mode;

    public Sm4Encryptor(String key, String mode, String iv) {
        this.mode = mode.toUpperCase();
        String hexKey = HexUtil.patchHexString(key, 16);
        this.keyBytes = hexKey.getBytes(StandardCharsets.UTF_8);
        this.context = new SM4Context();
        this.context.isPadding = true;

        if ("ECB".equals(this.mode)) {
            this.ivBytes = null;
        } else if ("CBC".equals(this.mode)) {
            if (iv == null) {
                throw new ValidationException(ErrorCode.PARAM_MISSING, new Object[]{"iv"});
            }
            String hexIv = HexUtil.patchHexString(iv, 16);
            this.ivBytes = hexIv.getBytes(StandardCharsets.UTF_8);
        } else {
            throw new ValidationException(ErrorCode.PARAM_INVALID, new Object[]{"mode"});
        }

        try {
            this.context.mode = SM4.SM4_ENCRYPT;
            sm4.sm4_setkey_enc(this.context, this.keyBytes);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.ENCRYPT_ERROR, e);
        }
    }

    @Override
    public byte[] encrypt(byte[] data) {
        if (data == null) return null;
        try {
            if ("ECB".equals(mode)) {
                return sm4.sm4_crypt_ecb(context, data);
            } else {
                return sm4.sm4_crypt_cbc(context, ivBytes, data);
            }
        } catch (Exception e) {
            return data;
        }
    }

    @Override
    public byte[] decrypt(byte[] data) {
        if (data == null) return null;
        try {
            SM4Context decCtx = new SM4Context();
            decCtx.isPadding = true;
            decCtx.mode = SM4.SM4_DECRYPT;
            sm4.sm4_setkey_dec(decCtx, this.keyBytes);

            if ("ECB".equals(mode)) {
                return sm4.sm4_crypt_ecb(decCtx, data);
            } else {
                return sm4.sm4_crypt_cbc(decCtx, ivBytes, data);
            }
        } catch (Exception e) {
            return data;
        }
    }
}
