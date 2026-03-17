package com.agile.pdf.core.util;

import com.agile.common.util.ExceptionUtil;
import com.agile.pdf.core.model.PdfWaterInfo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * PDF 水印工具类，支持文字水印平铺。
 * 水印参数通过 {@link PdfWaterInfo} 封装，默认使用 iText 亚洲字体包（支持中文）。
 * 所有方法输入输出均为字节数组，异常时返回原始数组。
 */
public class PdfWatermarkUtil {

    private static final Logger log = LoggerFactory.getLogger(PdfWatermarkUtil.class);

    // 默认字体名称和编码（iText 亚洲字体包）
    private static final String DEFAULT_FONT_NAME = "STSong-Light";
    private static final String DEFAULT_FONT_ENCODING = "UniGB-UCS2-H";
    private static final boolean DEFAULT_FONT_EMBEDDED = BaseFont.EMBEDDED;

    private PdfWatermarkUtil() {}

    // 获取默认 BaseFont（每次调用创建，无缓存需求，因为字体不变且轻量）
    private static BaseFont getDefaultBaseFont() throws IOException, DocumentException {
        return BaseFont.createFont(DEFAULT_FONT_NAME, DEFAULT_FONT_ENCODING, DEFAULT_FONT_EMBEDDED);
    }

    // -------------------------------------------------------------------------
    // 公共水印方法
    // -------------------------------------------------------------------------

    /**
     * 为 PDF 字节数组添加文字水印（使用参数封装类）。
     *
     * @param pdfBytes 原始 PDF 字节数组
     * @param info     水印参数
     * @return 添加水印后的 PDF 字节数组，异常时返回原始数组
     */
    public static byte[] addPdfWatermark(byte[] pdfBytes, PdfWaterInfo info) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            log.warn("输入 PDF 字节数组为空，直接返回");
            return pdfBytes;
        }
        if (info == null || info.getText() == null || info.getText().trim().isEmpty()) {
            log.warn("水印信息无效，直接返回原 PDF");
            return pdfBytes;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfReader reader = null;
        PdfStamper stamper = null;

        try {
            reader = new PdfReader(pdfBytes);
            stamper = new PdfStamper(reader, baos);

            // 获取默认字体
            BaseFont baseFont = getDefaultBaseFont();
            float textWidth = baseFont.getWidthPoint(info.getText(), info.getFontSize());
            float textHeight = info.getFontSize(); // 近似高度

            // 设置透明度
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(info.getOpacity());
            gs.setStrokeOpacity(0.2f); // 保持原固定笔画透明度

            // 设置颜色（默认为黑色）
            BaseColor color = info.getColor() != null ? info.getColor() : BaseColor.BLACK;

            int totalPages = reader.getNumberOfPages();
            log.debug("PDF 总页数: {}", totalPages);

            for (int i = 1; i <= totalPages; i++) {
                Rectangle pageRect = reader.getPageSizeWithRotation(i);
                PdfContentByte over = stamper.getOverContent(i);
                over.saveState();
                over.setGState(gs);
                over.setColorFill(color);
                over.beginText();
                over.setFontAndSize(baseFont, info.getFontSize());

                float pageHeight = pageRect.getHeight();
                float pageWidth = pageRect.getWidth();

                // 计算步长（与老代码一致）
                float stepX = textWidth * info.getWidthDensity();
                float stepY = textHeight * info.getHeightDensity();

                // 调整循环范围和绘制偏移，使水印位置与老代码一致
                // 老代码循环范围：y 从 textHeight 到 pageHeight * 2（或 1.5），x 从 textWidth 到 pageWidth * 1.5 + textWidth
                // 绘制时减去 textWidth/textHeight 并加上固定偏移 50 和 40
                float startY = textHeight;
                float endY = pageHeight * 1.5f;
                float startX = textWidth;
                float endX = pageWidth * 1.5f + textWidth;

                for (float y = startY; y < endY; y += stepY) {
                    for (float x = startX; x < endX; x += stepX) {
                        // 应用与老代码相同的偏移量
                        over.showTextAligned(Element.ALIGN_LEFT, info.getText(),
                                x - textWidth + 50, y - textHeight + 40, info.getAngle());
                    }
                }

                over.endText();
                over.restoreState();
            }

            stamper.close(); // 关闭 stamper 会同时关闭 reader（如果 reader 未被其他 stamper 使用）
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("添加 PDF 水印失败: {}", ExceptionUtil.getExceptionStr(e));
            return pdfBytes;
        } finally {
            // 确保资源释放
            if (stamper != null) {
                try {
                    stamper.close();
                } catch (Exception e) {
                    log.warn("关闭 PdfStamper 失败: {}", ExceptionUtil.getExceptionStr(e));
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    log.warn("关闭 PdfReader 失败: {}", ExceptionUtil.getExceptionStr(e));
                }
            }
            // ByteArrayOutputStream 无需关闭
        }
    }
}