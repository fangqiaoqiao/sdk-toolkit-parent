package com.agile.image.core.util;

import com.agile.common.enums.ErrorCode;
import com.agile.common.exception.SystemException;
import com.agile.common.font.FontLoader;
import com.agile.image.core.enums.ImageFormat;
import com.agile.common.util.ExceptionUtil;
import com.agile.image.core.model.ImageWaterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片水印工具类，支持平铺水印、高级双水印和信息水印。
 * 所有操作基于内存中的 BufferedImage，输入输出均为图像对象。
 * 字体管理委托给 {@link FontLoader}，支持外部字体配置。
 */
public final class ImageWatermarkUtil {

    private static final Logger log = LoggerFactory.getLogger(ImageWatermarkUtil.class);

    // 外部字体路径，为 null 时使用 FontLoader 默认字体
    private static volatile String watermarkFontPath;

    private ImageWatermarkUtil() {
    }

    /**
     * 设置外部水印字体路径。
     *
     * @param fontPath 字体文件路径，为 null 或空表示使用默认字体
     */
    public static void setWatermarkFont(String fontPath) {
        watermarkFontPath = fontPath;
        Font base = getBaseWatermarkFont();
        log.info("水印字体路径已设置为: {}, 实际使用字体: {}", fontPath, base.getFontName());
    }

    // 获取基础水印字体（未调整大小）
    private static Font getBaseWatermarkFont() {
        return FontLoader.getBaseFont(watermarkFontPath);
    }

    // 获取指定大小的默认水印字体
    private static Font getDefaultWatermarkFont(float size) {
        return getBaseWatermarkFont().deriveFont(size);
    }

    // -------------------------------------------------------------------------
    // 核心水印方法（基于 BufferedImage）
    // -------------------------------------------------------------------------

    /**
     * 平铺水印：在图片上均匀铺满水印文字。
     *
     * @param src        原始图像
     * @param waterInfo  水印信息（x = 水平间距，y = 垂直间距）
     * @param format     输出图片格式（用于确定画布类型：PNG 保留透明度，RGB 无透明度）
     * @return 添加水印后的图像，异常时返回原始 src
     */
    public static BufferedImage addTileWatermark(BufferedImage src,
                                                 ImageWaterInfo waterInfo,
                                                 ImageFormat format) {
        try {
            return drawTileWatermark(src, waterInfo, format);
        } catch (Exception e) {
            log.error("添加平铺水印失败: {}", ExceptionUtil.getExceptionStr(e));
            return src;
        }
    }

    /**
     * 高级双水印：第一个水印按特殊循环平铺，第二个水印单行绘制。
     *
     * @param src        原始图像
     * @param waterInfo1 第一个水印信息（x、y 作为平铺步长）
     * @param waterInfo2 第二个水印信息（x、y 作为绘制坐标）
     * @param format     输出图片格式
     * @return 添加水印后的图像，异常时返回原始 src
     */
    public static BufferedImage addDoubleWatermark(BufferedImage src,
                                                   ImageWaterInfo waterInfo1,
                                                   ImageWaterInfo waterInfo2,
                                                   ImageFormat format) {
        try {
            return drawDoubleWatermark(src, waterInfo1, waterInfo2, format);
        } catch (Exception e) {
            log.error("添加双水印失败: {}", ExceptionUtil.getExceptionStr(e));
            return src;
        }
    }

    /**
     * 信息水印：左上角和右下角绘制水印，并添加额外信息。
     *
     * @param src       原始图像
     * @param waterInfo 水印信息（x、y 字段未使用）
     * @param format    输出图片格式
     * @return 添加水印后的图像，异常时返回原始 src
     */
    public static BufferedImage addInfoWatermark(BufferedImage src,
                                                 ImageWaterInfo waterInfo,
                                                 ImageFormat format) {
        try {
            return drawInfoWatermark(src, waterInfo, format);
        } catch (Exception e) {
            log.error("添加信息水印失败: {}", ExceptionUtil.getExceptionStr(e));
            return src;
        }
    }

    // -------------------------------------------------------------------------
    // 私有绘制逻辑（不再抛出异常，由公共方法捕获）
    // -------------------------------------------------------------------------

    private static BufferedImage drawTileWatermark(BufferedImage src,
                                                   ImageWaterInfo info,
                                                   ImageFormat format) {
        int width = src.getWidth(), height = src.getHeight();
        BufferedImage dest = createCanvas(width, height, format);
        Graphics2D g = dest.createGraphics();
        try {
            g.drawImage(src, 0, 0, width, height, null);

            Color color = info.getColor() != null ? info.getColor() : Color.BLACK;
            g.setColor(color);

            Font font = info.getFont() != null ? info.getFont() : getDefaultWatermarkFont(22);
            g.setFont(font);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, info.getOpacity()));
            applyRotation(g, info.getDegree(), width, height);

            FontMetrics metrics = g.getFontMetrics();
            int textW = metrics.stringWidth(info.getText());
            int textH = metrics.getHeight();

            int xSpace = info.getX();
            int ySpace = info.getY();

            int cols = (int) Math.ceil((double) width / (textW + xSpace)) + 1;
            int rows = (int) Math.ceil((double) height / (textH + ySpace)) + 1;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int x = c * (textW + xSpace);
                    int y = r * (textH + ySpace);
                    g.drawString(info.getText(), x, y);
                }
            }
            return dest;
        } finally {
            g.dispose();
        }
    }

    private static BufferedImage drawDoubleWatermark(BufferedImage src,
                                                     ImageWaterInfo info1,
                                                     ImageWaterInfo info2,
                                                     ImageFormat format) {
        int width = src.getWidth(), height = src.getHeight();
        BufferedImage dest = createCanvas(width, height, format);
        Graphics2D g = null;
        try {
            // 第一个水印（特殊平铺）
            g = dest.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);

            g.setColor(info1.getColor() != null ? info1.getColor() : Color.BLACK);
            Font font1 = info1.getFont() != null ? info1.getFont() : getDefaultWatermarkFont(22);
            g.setFont(font1);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
            applyRotation(g, info1.getDegree(), width, height);

            FontMetrics metrics = g.getFontMetrics();
            int textW = metrics.stringWidth(info1.getText());
            int textH = metrics.getHeight();
            int cols = Math.max(1, width / textW);
            int rows = Math.max(1, height / textH);
            int stepX = info1.getX();
            int stepY = info1.getY();
            for (int i = 0; i < cols * 10; i++) {
                for (int j = -10; j < rows * 10; j++) {
                    g.drawString(info1.getText(), i * stepX, j * stepY);
                }
            }
            g.dispose();
            g = null;

            // 第二个水印（单行）
            Graphics2D g2 = dest.createGraphics();
            g2.drawImage(dest, 0, 0, width, height, null);
            g2.setColor(info2.getColor() != null ? info2.getColor() : Color.BLACK);
            Font font2 = info2.getFont() != null ? info2.getFont() : getDefaultWatermarkFont(14);
            g2.setFont(font2);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
            g2.rotate(0, width / 2.0, height / 2.0);
            g2.drawString(info2.getText(), info2.getX(), info2.getY());
            g2.dispose();
        } catch (Exception e) {
            if (g != null) g.dispose();
            // 重新抛出，由公共方法统一处理
            throw new SystemException(ErrorCode.IO_ERROR.getCode(), "绘制双水印失败", e);
        }
        return dest;
    }

    private static BufferedImage drawInfoWatermark(BufferedImage src,
                                                   ImageWaterInfo info,
                                                   ImageFormat format) {
        int width = src.getWidth(), height = src.getHeight();
        BufferedImage dest = createCanvas(width, height, format);
        Graphics2D g = null;
        try {
            g = dest.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);

            Color color = info.getColor() != null ? info.getColor() : new Color(133, 133, 133);
            g.setColor(color);

            Font font = info.getFont() != null ? info.getFont() : getDefaultWatermarkFont(22);
            g.setFont(font);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, info.getOpacity()));
            applyRotation(g, info.getDegree(), width, height);

            FontMetrics metrics = g.getFontMetrics();
            int textW = metrics.stringWidth(info.getText());
            String tripleText = info.getText() + info.getText() + info.getText();

            g.drawString(tripleText, 120, 0);
            g.drawString(tripleText, width - 3 * textW, height);
            g.dispose();
            g = null;
        } catch (Exception e) {
            if (g != null) g.dispose();
            throw new SystemException(ErrorCode.IO_ERROR.getCode(), "绘制信息水印失败", e);
        }
        return dest;
    }

    // -------------------------------------------------------------------------
    // 辅助方法
    // -------------------------------------------------------------------------

    private static BufferedImage createCanvas(int width, int height, ImageFormat format) {
        int imageType = format.isPng() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        return new BufferedImage(width, height, imageType);
    }

    private static void applyRotation(Graphics2D g, Integer angle, int width, int height) {
        if (angle != null && angle != 0) {
            g.rotate(Math.toRadians(angle), width / 2.0, height / 2.0);
        }
    }
}