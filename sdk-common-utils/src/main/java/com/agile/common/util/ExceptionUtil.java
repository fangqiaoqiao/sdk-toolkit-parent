package com.agile.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    /**
     * 获取异常的堆栈字符串。
     *
     * @param throwable 异常
     * @return 堆栈字符串
     */
    public static String getExceptionStr(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

}
