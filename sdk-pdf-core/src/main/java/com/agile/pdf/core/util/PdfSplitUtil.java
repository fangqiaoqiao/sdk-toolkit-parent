package com.agile.pdf.core.util;

import com.agile.pdf.core.finder.PdfKeywordFinder;
import com.agile.pdf.core.model.PdfKeywordPosition;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PDF 分割工具：根据关键字定位页码并分割文档
 */
public class PdfSplitUtil {

    /**
     * 提取关键字之前的所有页面。
     *
     * @param pdfData PDF 文件字节数组
     * @param keyword 要查找的关键字
     * @return 提取的 PDF 字节数组；如果关键字不存在或没有前导页，返回 null
     */
    public static byte[] extractPagesBeforeKeyword(byte[] pdfData, String keyword) {
        byte[][] parts = splitByKeyword(pdfData, keyword);
        return parts[0];
    }

    /**
     * 提取关键字所在页及之后的所有页面。
     *
     * @param pdfData PDF 文件字节数组
     * @param keyword 要查找的关键字
     * @return 提取的 PDF 字节数组；如果关键字不存在或发生异常，返回 null
     */
    public static byte[] extractPagesFromKeyword(byte[] pdfData, String keyword) {
        byte[][] parts = splitByKeyword(pdfData, keyword);
        // splitByKeyword 返回的第二个元素即为关键字所在页及之后的页面
        return parts[1];
    }

    /**
     * 根据关键字分割 PDF，返回两部分字节数组。
     * 第一部分：关键字所在页之前的页面（始终非空，异常时返回原始 PDF）
     * 第二部分：关键字所在页及之后的页面（可能为 null，表示无此部分）
     *
     * @param pdfData PDF 文件字节数组
     * @param keyword 要查找的关键字
     * @return 包含两个字节数组的数组：[第一部分, 第二部分]
     */
    public static byte[][] splitByKeyword(byte[] pdfData, String keyword) {
        try {
            List<PdfKeywordPosition> positions = PdfKeywordFinder.findPositions(pdfData, keyword);
            return splitByKeyword(pdfData, positions);
        } catch (Exception e) {
            // 异常时返回原始 PDF 作为第一部分，第二部分为 null
            return new byte[][]{pdfData, null};
        }
    }

    /**
     * 根据已知关键字位置分割 PDF，返回两部分字节数组。
     * 第一部分：关键字所在页之前的页面（始终非空，异常时返回原始 PDF）
     * 第二部分：关键字所在页及之后的页面（可能为 null，表示无此部分）
     *
     * @param pdfData   PDF 文件字节数组
     * @param positions 关键字位置列表（允许为空或 null）
     * @return 包含两个字节数组的数组：[第一部分, 第二部分]
     */
    public static byte[][] splitByKeyword(byte[] pdfData, List<PdfKeywordPosition> positions) {
        if (positions == null || positions.isEmpty()) {
            // 无关键字位置，整个文档作为第一部分
            return new byte[][]{pdfData, null};
        }

        try {
            // 提取所有出现页码（去重）
            Set<Integer> pagesWithKeyword = new HashSet<>();
            for (PdfKeywordPosition pos : positions) {
                pagesWithKeyword.add(pos.getPageNum());
            }

            // 获取总页数
            PdfReader reader = new PdfReader(pdfData);
            int totalPages = reader.getNumberOfPages();
            reader.close();

            // 确定目标页码（取最大值，通常为最后一页）
            int targetPage = pagesWithKeyword.stream().max(Integer::compareTo).orElse(-1);
            if (targetPage < 1 || targetPage > totalPages) {
                return new byte[][]{pdfData, null};
            }

            // 分割
            byte[] part1 = null;
            if (targetPage > 1) {
                part1 = extractPages(pdfData, 1, targetPage - 1);
            } else {
                part1 = null; // 关键字在第一页，则第一部分无内容
            }
            byte[] part2 = extractPages(pdfData, targetPage, totalPages); // 始终非空

            // 注意：如果 part1 为 null，表示没有前导页，此时第一部分应为空（null），但调用方需理解 null 表示没有页面
            return new byte[][]{part1, part2};

        } catch (Exception e) {
            // 任何异常，返回原始 PDF 作为第一部分，第二部分为 null
            return new byte[][]{pdfData, null};
        }
    }

    /**
     * 提取 PDF 中指定页码范围（包含）的页面，返回字节数组
     *
     * @param pdfData   原 PDF 字节数组
     * @param startPage 起始页码（1-based）
     * @param endPage   结束页码
     * @return 提取的 PDF 字节数组，若 startPage > endPage 返回 null
     * @throws IOException      读取异常
     * @throws DocumentException PDF 操作异常
     */
    private static byte[] extractPages(byte[] pdfData, int startPage, int endPage) throws IOException, DocumentException {
        if (startPage > endPage) {
            return null;
        }
        PdfReader reader = new PdfReader(pdfData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, baos);
        document.open();
        for (int i = startPage; i <= endPage; i++) {
            copy.addPage(copy.getImportedPage(reader, i));
        }
        document.close();
        reader.close();
        return baos.toByteArray();
    }
}