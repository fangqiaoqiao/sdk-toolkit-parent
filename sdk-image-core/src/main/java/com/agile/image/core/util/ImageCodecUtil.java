package com.agile.image.core.util;

import com.agile.image.core.enums.ImageFormat;
import com.agile.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageCodecUtil {

    private static final Logger log = LoggerFactory.getLogger(ImageCodecUtil.class);

    private ImageCodecUtil() {}

    // ==================== 核心方法（字节数组 ↔ BufferedImage） ====================

    public static BufferedImage readFromBytes(byte[] imageData) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                throw new IOException("无法解析图片格式");
            }
            return image;
        } catch (IOException e) {
            log.error("从字节数组读取图片失败: {}", ExceptionUtil.getExceptionStr(e));
            throw e;
        }
    }

    public static byte[] writeToBytes(BufferedImage image, ImageFormat format) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            boolean success = ImageIO.write(image, format.toString(), baos);
            if (!success) {
                throw new IOException("不支持的图片格式: " + format);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("图片编码为字节数组失败: {}", ExceptionUtil.getExceptionStr(e));
            throw e;
        }
    }

    // ==================== 文件操作 ====================

    public static BufferedImage readFromFile(String filePath) throws IOException {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return readFromBytes(bytes);
        } catch (IOException e) {
            log.error("从文件读取图片失败: {}", ExceptionUtil.getExceptionStr(e));
            throw e;
        }
    }

    // ==================== Base64 操作 ====================

    public static BufferedImage decodeBase64ToImage(String base64) throws IOException {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            return readFromBytes(bytes);
        } catch (IOException e) {
            log.error("Base64 解码为图片失败: {}", ExceptionUtil.getExceptionStr(e));
            throw e;
        }
    }

    public static String encodeImageToBase64(BufferedImage image, ImageFormat format) throws IOException {
        byte[] bytes = writeToBytes(image, format);
        return Base64.getEncoder().encodeToString(bytes);
    }
}