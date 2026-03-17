package com.agile.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

}
