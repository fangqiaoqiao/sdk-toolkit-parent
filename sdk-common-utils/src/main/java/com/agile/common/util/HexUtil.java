package com.agile.common.util;

public class HexUtil {
    public static String byteArrToHexStr(byte[] bytes) {
        int iLen = bytes.length;
        StringBuilder sb = new StringBuilder(iLen * 2);
        for (byte b : bytes) {
            int intTmp = b & 0xFF;
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    public static byte[] hexStrToByteArr(String strIn) {
        byte[] arrB = strIn.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        int iLen = arrB.length;
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i += 2) {
            String strTmp = new String(arrB, i, 2, java.nio.charset.StandardCharsets.UTF_8);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * 补全字符串到指定长度（原 HexUtil.patchHexString）
     */
    public static String patchHexString(String str, int maxLength) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= maxLength) {
            return str.substring(0, maxLength);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLength - str.length(); i++) {
            sb.append("0");
        }
        return sb + str;
    }
}
