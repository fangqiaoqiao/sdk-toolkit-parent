package com.agile.image.core.util;

import com.agile.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片压缩工具类，支持按比例或指定尺寸缩放 BufferedImage。
 * 所有方法输入输出均为 BufferedImage，异常时返回原始图像。
 */
public class ImageCompressUtil {

    private static final Logger log = LoggerFactory.getLogger(ImageCompressUtil.class);

    private ImageCompressUtil() {
        // 工具类禁止实例化
    }

    /**
     * 按比例缩放图像。
     *
     * @param srcImage 原始图像（不可变，方法内不会修改）
     * @param rate     缩放比例，必须大于 0
     * @return 缩放后的新图像（BufferedImage），异常时返回原始 srcImage
     */
    public static BufferedImage scaleByRate(BufferedImage srcImage, double rate) {
        if (srcImage == null) {
            log.warn("输入图像为 null，直接返回 null");
            return null;
        }

        if (rate <= 0) {
            log.warn("缩放比例无效 (rate={})，必须大于 0，返回原始图像", rate);
            return srcImage;
        }

        int originalWidth = srcImage.getWidth();
        int originalHeight = srcImage.getHeight();
        int targetWidth = (int) (originalWidth * rate);
        int targetHeight = (int) (originalHeight * rate);

        return doScale(srcImage, targetWidth, targetHeight);
    }

    /**
     * 按指定尺寸缩放图像。
     *
     * @param srcImage 原始图像（不可变，方法内不会修改）
     * @param width    目标宽度，必须大于 0
     * @param height   目标高度，必须大于 0
     * @return 缩放后的新图像（BufferedImage），异常时返回原始 srcImage
     */
    public static BufferedImage scaleToSize(BufferedImage srcImage, int width, int height) {
        if (srcImage == null) {
            log.warn("输入图像为 null，直接返回 null");
            return null;
        }

        if (width <= 0 || height <= 0) {
            log.warn("目标尺寸无效 (width={}, height={})，返回原始图像", width, height);
            return srcImage;
        }

        return doScale(srcImage, width, height);
    }

    /**
     * 执行实际的图像缩放操作。
     *
     * @param srcImage      原始图像
     * @param targetWidth   目标宽度
     * @param targetHeight  目标高度
     * @return 缩放后的新图像，异常时返回原始图像
     */
    private static BufferedImage doScale(BufferedImage srcImage, int targetWidth, int targetHeight) {
        int originalWidth = srcImage.getWidth();
        int originalHeight = srcImage.getHeight();

        // 创建目标图像（使用 RGB 类型，可改为根据原图类型动态选择）
        BufferedImage destImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = destImage.createGraphics();
        try {
            // 设置渲染提示以提高缩放质量
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
            log.debug("图像缩放完成: {}x{} -> {}x{}", originalWidth, originalHeight, targetWidth, targetHeight);
            return destImage;
        } catch (Exception e) {
            log.error("缩放图像失败: {}", ExceptionUtil.getExceptionStr(e));
            return srcImage;
        } finally {
            g.dispose();
        }
    }
}