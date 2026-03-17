package com.agile.common.encrypt.impl;

import com.agile.common.encrypt.Encryptor;
import com.agile.common.encrypt.support.DesCore;
import com.agile.common.enums.ErrorCode;
import com.agile.common.exception.SystemException;

public class DesEncryptor implements Encryptor {

    private final DesCore desCore;

    public DesEncryptor(String key) {
        try {
            this.desCore = new DesCore(key);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.KEY_INVALID, e);
        }
    }

    @Override
    public byte[] encrypt(byte[] data) {
        if (data == null) return null;
        try {
            return desCore.encrypt(data);
        } catch (Exception e) {
            return data;
        }
    }

    @Override
    public byte[] decrypt(byte[] data) {
        if (data == null) return null;
        try {
            return desCore.decrypt(data);
        } catch (Exception e) {
            return data;
        }
    }

}
