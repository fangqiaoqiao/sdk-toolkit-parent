package com.agile.common.encrypt.support.sm4;

public class Sm4Padding {

    public static byte[] addPadding(byte[] input) {
        int p = 16 - input.length % 16;
        byte[] ret = new byte[input.length + p];
        System.arraycopy(input, 0, ret, 0, input.length);
        for (int i = 0; i < p; i++) {
            ret[input.length + i] = (byte) p;
        }
        return ret;
    }

    public static byte[] removePadding(byte[] input) {
        int p = input[input.length - 1] & 0xFF;
        if (p > 16) {
            throw new IllegalArgumentException("Invalid padding");
        }
        byte[] ret = new byte[input.length - p];
        System.arraycopy(input, 0, ret, 0, input.length - p);
        return ret;
    }

}
