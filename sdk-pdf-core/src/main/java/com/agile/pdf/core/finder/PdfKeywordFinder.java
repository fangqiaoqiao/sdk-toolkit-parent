package com.agile.pdf.core.finder;

import com.agile.pdf.core.model.PdfKeywordPosition;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF 关键字位置查找器
 */
public class PdfKeywordFinder {

    /**
     * 在 PDF 文档中查找指定关键字的所有出现位置
     *
     * @param pdfData PDF 文件的字节数组
     * @param keyword 要查找的关键字
     * @return 关键字位置列表（每个关键字取第一个字符的位置）
     * @throws IOException 解析异常
     */
    public static List<PdfKeywordPosition> findPositions(byte[] pdfData, String keyword) throws IOException {
        List<PdfKeywordPosition> result = new ArrayList<>();
        List<PdfPageContentPositions> pageContentPositions = getPdfContentPositionsList(pdfData);

        for (PdfPageContentPositions pageContentPosition : pageContentPositions) {
            List<PdfKeywordPosition> positions = findPositionsInPage(keyword, pageContentPosition);
            if (positions != null && !positions.isEmpty()) {
                result.addAll(positions);
            }
        }
        return result;
    }

    private static List<PdfPageContentPositions> getPdfContentPositionsList(byte[] pdfData) throws IOException {
        PdfReader reader = new PdfReader(pdfData);
        try {
            List<PdfPageContentPositions> result = new ArrayList<>();
            int pages = reader.getNumberOfPages();
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                float width = reader.getPageSize(pageNum).getWidth();
                float height = reader.getPageSize(pageNum).getHeight();

                PdfRenderListener listener = new PdfRenderListener(pageNum, width, height);
                PdfContentStreamProcessor processor = new PdfContentStreamProcessor(listener);
                PdfDictionary pageDic = reader.getPageN(pageNum);
                PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
                processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pageNum), resourcesDic);

                String content = listener.getContent();
                List<PdfKeywordPosition> positions = listener.getCharPositions();

                PdfPageContentPositions pageContentPositions = new PdfPageContentPositions();
                pageContentPositions.setContent(content);
                pageContentPositions.setPositions(positions);

                result.add(pageContentPositions);
            }
            return result;
        } finally {
            reader.close();
        }
    }

    private static List<PdfKeywordPosition> findPositionsInPage(String keyword, PdfPageContentPositions pageContentPositions) {
        List<PdfKeywordPosition> result = new ArrayList<>();
        String content = pageContentPositions.getContent();
        List<PdfKeywordPosition> charPositions = pageContentPositions.getPositions();

        for (int pos = 0; pos < content.length(); ) {
            int positionIndex = content.indexOf(keyword, pos);
            if (positionIndex == -1) {
                break;
            }
            // 取关键字第一个字符的位置（原逻辑如此）
            result.add(charPositions.get(positionIndex));
            pos = positionIndex + 1;
        }
        return result;
    }

    /**
     * 单页内容和字符位置集合
     */
    private static class PdfPageContentPositions {
        private String content;
        private List<PdfKeywordPosition> positions;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<PdfKeywordPosition> getPositions() {
            return positions;
        }

        public void setPositions(List<PdfKeywordPosition> positions) {
            this.positions = positions;
        }
    }

    /**
     * 渲染监听器，提取字符内容和位置
     */
    private static class PdfRenderListener implements RenderListener {
        private final int pageNum;
        private final float pageWidth;
        private final float pageHeight;
        private final StringBuilder contentBuilder = new StringBuilder();
        private final List<PdfKeywordPosition> charPositions = new ArrayList<>();

        public PdfRenderListener(int pageNum, float pageWidth, float pageHeight) {
            this.pageNum = pageNum;
            this.pageWidth = pageWidth;
            this.pageHeight = pageHeight;
        }

        @Override
        public void beginTextBlock() {
        }

        @Override
        public void renderText(TextRenderInfo renderInfo) {
            List<TextRenderInfo> characterRenderInfos = renderInfo.getCharacterRenderInfos();
            for (TextRenderInfo textRenderInfo : characterRenderInfos) {
                String word = textRenderInfo.getText();
                if (word.length() > 1) {
                    word = word.substring(word.length() - 1);
                }
                Rectangle2D.Float rectangle = textRenderInfo.getAscentLine().getBoundingRectange();
                charPositions.add(new PdfKeywordPosition(pageNum, rectangle.x, rectangle.y));
                contentBuilder.append(word);
            }
        }

        @Override
        public void endTextBlock() {
        }

        @Override
        public void renderImage(ImageRenderInfo renderInfo) {
        }

        public String getContent() {
            return contentBuilder.toString();
        }

        public List<PdfKeywordPosition> getCharPositions() {
            return charPositions;
        }
    }
}