package com.agile.pdf.core.util;

import com.agile.common.util.ExceptionUtil;
import com.agile.pdf.core.finder.PdfKeywordFinder;
import com.agile.pdf.core.model.FixedBlurConfig;
import com.agile.pdf.core.model.OffsetBlurConfig;
import com.agile.pdf.core.model.PdfKeywordPosition;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * PDF 模糊化工具（在指定位置覆盖图片）
 */
public class PdfBlurUtil {

    private static final Logger log = LoggerFactory.getLogger(PdfBlurUtil.class);

    /**
     * 对 PDF 字节数据中包含指定关键字的页面进行模糊处理，在每页的固定位置覆盖图片
     *
     * @param srcPdfData 源 PDF 字节数组
     * @param keyword    需要查找的关键字
     * @param config     固定位置模糊配置（必须包含图片路径、坐标和缩放比例），若为 null 则直接返回原始数据
     * @return 处理后的 PDF 字节数组，若发生异常或 config 为 null 则返回原始 srcPdfData
     */
    public static byte[] blurPdf(byte[] srcPdfData, String keyword, FixedBlurConfig config) {
        try {
            List<PdfKeywordPosition> positions = PdfKeywordFinder.findPositions(srcPdfData, keyword);
            return blurPdf(srcPdfData, positions, config);
        } catch (Exception e) {
            log.error("Failed to find positions for keyword '{}': {}", keyword, ExceptionUtil.getExceptionStr(e));
            return srcPdfData;
        }
    }

    /**
     * 对 PDF 字节数据中包含指定关键字的页面进行模糊处理，在每页的固定位置覆盖图片
     *
     * @param srcPdfData 源 PDF 字节数组
     * @param keyword    需要查找的关键字
     * @param config     偏移模糊配置（包含图片路径、缩放、偏移量），若为 null 则直接返回原始数据
     * @return 处理后的 PDF 字节数组，若发生异常或 config 为 null 则返回原始 srcPdfData
     */
    public static byte[] blurPdf(byte[] srcPdfData, String keyword, OffsetBlurConfig config) {
        try {
            List<PdfKeywordPosition> positions = PdfKeywordFinder.findPositions(srcPdfData, keyword);
            return blurPdf(srcPdfData, positions, config);
        } catch (Exception e) {
            log.error("Failed to find positions for keyword '{}': {}", keyword, ExceptionUtil.getExceptionStr(e));
            return srcPdfData;
        }
    }

    /**
     * 根据已知关键字位置对 PDF 进行模糊处理，在指定页的固定位置覆盖图片
     * <p>
     * <b>注意：</b>此方法使用 {@link FixedBlurConfig} 中配置的坐标 (x, y) 放置图片，
     * 并不会使用 {@link PdfKeywordPosition} 中的坐标。
     *
     * @param srcPdfData 源 PDF 字节数组
     * @param positions  关键字位置列表（仅用于确定页码，坐标被忽略）
     * @param config     固定位置模糊配置（必须包含图片路径、坐标和缩放比例）
     * @return 处理后的 PDF 字节数组，若发生异常或参数无效则返回原始 srcPdfData
     */
    public static byte[] blurPdf(byte[] srcPdfData, List<PdfKeywordPosition> positions, FixedBlurConfig config) {
        if (positions == null || positions.isEmpty()) {
            log.info("No positions provided for blur, skip.");
            return srcPdfData;
        }
        // 提取所有页码
        int[] pages = positions.stream().mapToInt(PdfKeywordPosition::getPageNum).distinct().toArray();
        return applyBlurToPages(srcPdfData, config, pages);
    }

    /**
     * 根据坐标列表在 PDF 的指定位置动态添加模糊图片（基于关键字坐标加偏移量）。
     * 每个位置使用 {@link PdfKeywordPosition#getX()} 和 {@link PdfKeywordPosition#getY()} 作为基准，
     * 并加上配置中指定的偏移量，缩放比例也由配置统一控制。
     *
     * @param srcPdfData 源 PDF 字节数组
     * @param positions  位置列表（不能为空）
     * @param config     偏移模糊配置（包含图片路径、缩放、偏移量）
     * @return 处理后的 PDF 字节数组，若发生异常或参数无效则返回原始 srcPdfData
     */
    public static byte[] blurPdf(byte[] srcPdfData,
                                 List<PdfKeywordPosition> positions,
                                 OffsetBlurConfig config) {
        if (srcPdfData == null || srcPdfData.length == 0) {
            log.warn("Input PDF data is empty, return original.");
            return srcPdfData;
        }
        if (positions == null || positions.isEmpty()) {
            log.info("No positions provided for blur, skip.");
            return srcPdfData;
        }
        if (config == null) {
            log.warn("OffsetBlurConfig is null, skip blur and return original data.");
            return srcPdfData;
        }

        PdfReader reader = null;
        PdfStamper stamper = null;
        ByteArrayOutputStream baos = null;

        try {
            reader = new PdfReader(srcPdfData);
            baos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, baos);

            Image image = Image.getInstance(config.getCoverImagePath());
            image.scalePercent(config.getScaleWidthPercent(), config.getScaleHeightPercent());

            for (PdfKeywordPosition pos : positions) {
                float x = pos.getX() + config.getXOffset();
                float y = pos.getY() + config.getYOffset();
                image.setAbsolutePosition(x, y);

                PdfContentByte overContent = stamper.getOverContent(pos.getPageNum());
                overContent.addImage(image);
                overContent.stroke();

                log.debug("Added blur image at page {}, x={}, y={}", pos.getPageNum(), x, y);
            }

            stamper.close();
            stamper = null;
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error while applying blur at positions: {}", ExceptionUtil.getExceptionStr(e));
            return srcPdfData;
        } finally {
            closeResources(stamper, reader, baos);
        }
    }

    /**
     * 对 PDF 的所有页面进行模糊处理，在每一页的固定位置覆盖图片
     *
     * @param srcPdfData 源 PDF 字节数组
     * @param config     固定位置模糊配置（必须包含图片路径、坐标和缩放比例）
     * @return 处理后的 PDF 字节数组，若发生异常或参数无效则返回原始 srcPdfData
     */
    public static byte[] blurAllPages(byte[] srcPdfData, FixedBlurConfig config) {
        if (srcPdfData == null || srcPdfData.length == 0) {
            log.warn("Input PDF data is empty, return original.");
            return srcPdfData;
        }
        if (config == null) {
            log.warn("FixedBlurConfig is null, skip blur and return original data.");
            return srcPdfData;
        }

        // 获取总页数（手动关闭 reader，兼容 JDK 1.8）
        PdfReader reader = null;
        int totalPages;
        try {
            reader = new PdfReader(srcPdfData);
            totalPages = reader.getNumberOfPages();
        } catch (IOException e) {
            log.error("Failed to read PDF to get total pages: {}", ExceptionUtil.getExceptionStr(e));
            return srcPdfData;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    log.error("Failed to close PdfReader: {}", ExceptionUtil.getExceptionStr(e));
                }
            }
        }

        // 生成所有页码数组
        int[] pages = new int[totalPages];
        for (int i = 0; i < totalPages; i++) {
            pages[i] = i + 1;
        }
        return applyBlurToPages(srcPdfData, config, pages);
    }

    /**
     * 内部核心方法：对指定的页码列表应用固定位置模糊
     *
     * @param srcPdfData 源 PDF 字节数组
     * @param config     固定位置模糊配置
     * @param pages      要处理的页码数组（不能为空）
     * @return 处理后的 PDF 字节数组，失败时返回原始数据
     */
    private static byte[] applyBlurToPages(byte[] srcPdfData, FixedBlurConfig config, int[] pages) {
        if (pages == null || pages.length == 0) {
            log.info("No pages specified for blur, skip.");
            return srcPdfData;
        }

        PdfReader reader = null;
        PdfStamper stamper = null;
        ByteArrayOutputStream baos = null;

        try {
            reader = new PdfReader(srcPdfData);
            baos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, baos);

            Image image = Image.getInstance(config.getCoverImagePath());
            image.scalePercent(config.getScaleWidthPercent(), config.getScaleHeightPercent());
            image.setAbsolutePosition(config.getX(), config.getY());

            for (int pageNum : pages) {
                PdfContentByte overContent = stamper.getOverContent(pageNum);
                overContent.addImage(image);
                overContent.stroke();
            }

            stamper.close();
            stamper = null;
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error while applying blur to PDF: {}", ExceptionUtil.getExceptionStr(e));
            return srcPdfData;
        } finally {
            closeResources(stamper, reader, baos);
        }
    }

    /**
     * 统一关闭资源
     */
    private static void closeResources(PdfStamper stamper, PdfReader reader, ByteArrayOutputStream baos) {
        if (stamper != null) {
            try {
                stamper.close();
            } catch (Exception e) {
                log.error("Failed to close PdfStamper: {}", ExceptionUtil.getExceptionStr(e));
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                log.error("Failed to close PdfReader: {}", ExceptionUtil.getExceptionStr(e));
            }
        }
        if (baos != null) {
            try {
                baos.close();
            } catch (IOException e) {
                log.error("Failed to close ByteArrayOutputStream: {}", ExceptionUtil.getExceptionStr(e));
            }
        }
    }
}