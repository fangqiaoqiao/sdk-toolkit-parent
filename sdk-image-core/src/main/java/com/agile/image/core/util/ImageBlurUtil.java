package com.agile.image.core.util;

import com.agile.common.enums.ErrorCode;
import com.agile.common.exception.ValidationException;
import com.agile.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageBlurUtil {

    private static final Logger log = LoggerFactory.getLogger(ImageBlurUtil.class);

    private ImageBlurUtil() {
        // 工具类禁止实例化
    }

    /**
     * 将覆盖图层绘制到底图的副本上，实现模糊效果（覆盖图应为模糊处理后的图像）。
     * 覆盖图将被缩放至与底图相同尺寸。原图不被修改。
     *
     * @param baseImage     底图（原始图片）
     * @param overlayImage  覆盖图层（模糊后的图片）
     * @return 绘制后的新图像（若异常则返回原始 baseImage）
     */
    public static BufferedImage applyBlurOverlay(BufferedImage baseImage, BufferedImage overlayImage) {
        return applyBlurOverlay(baseImage, overlayImage, 0, 0);
    }

    /**
     * 将覆盖图层绘制到底图的副本的指定位置，实现模糊效果。
     * 覆盖图将被缩放至底图尺寸（忽略 x,y 对应的宽高缩放）。原图不被修改。
     *
     * @param baseImage     底图（原始图片）
     * @param overlayImage  覆盖图层（模糊后的图片）
     * @param x             绘制起始 x 坐标
     * @param y             绘制起始 y 坐标
     * @return 绘制后的新图像（若异常则返回原始 baseImage）
     */
    public static BufferedImage applyBlurOverlay(BufferedImage baseImage,
                                                 BufferedImage overlayImage,
                                                 int x, int y) {
        if (baseImage == null || overlayImage == null) {
            throw new ValidationException(ErrorCode.PARAM_INVALID,
                    new Object[]{"底图和覆盖图不能为 null"});
        }

        int width = baseImage.getWidth();
        int height = baseImage.getHeight();

        // 创建原图副本
        BufferedImage result = new BufferedImage(width, height, baseImage.getType());
        Graphics2D g2d = result.createGraphics();
        try {
            // 先将原图绘制到副本
            g2d.drawImage(baseImage, 0, 0, width, height, null);
            // 再绘制覆盖图
            g2d.drawImage(overlayImage, x, y, width, height, null);
            log.debug("模糊覆盖层绘制完成，位置({},{}), 尺寸{}x{}", x, y, width, height);
            return result;
        } catch (Exception e) {
            log.error("绘制模糊覆盖层失败: {}", ExceptionUtil.getExceptionStr(e));
            return baseImage; // 异常时返回原始图像
        } finally {
            g2d.dispose();
        }
    }
}