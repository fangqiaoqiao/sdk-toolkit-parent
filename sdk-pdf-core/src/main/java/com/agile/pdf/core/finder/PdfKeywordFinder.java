package com.agile.pdf.core.finder;

import com.agile.pdf.core.model.PdfKeywordPosition;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF 关键字位置查找器（精简版）
 * 返回包含关键字的整个文本片段的基线左下角坐标
 */
public class PdfKeywordFinder {
    private static final Logger log = LoggerFactory.getLogger(PdfKeywordFinder.class);

    public static List<PdfKeywordPosition> findPositions(byte[] pdfData, String keyword) throws IOException {
        List<PdfKeywordPosition> result = new ArrayList<>();
        List<PdfPageContentPositions> pageContentPositions = getPdfContentPositionsList(pdfData);

        for (int i = 0; i < pageContentPositions.size(); i++) {
            PdfPageContentPositions pageContentPosition = pageContentPositions.get(i);
            List<PdfKeywordPosition> positions = findPositionsInPage(keyword, pageContentPosition, i + 1);
            if (!positions.isEmpty()) {
                result.addAll(positions);
            }
        }

        log.info("关键字 '{}' 查找完成，共找到 {} 个位置", keyword, result.size());
        return result;
    }

    private static List<PdfPageContentPositions> getPdfContentPositionsList(byte[] pdfData) throws IOException {
        PdfReader reader = new PdfReader(pdfData);
        try {
            List<PdfPageContentPositions> result = new ArrayList<>();
            int pages = reader.getNumberOfPages();

            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                PdfRenderListener listener = new PdfRenderListener(pageNum);
                PdfContentStreamProcessor processor = new PdfContentStreamProcessor(listener);
                PdfDictionary pageDic = reader.getPageN(pageNum);
                PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);

                byte[] pageContent = ContentByteUtils.getContentBytesForPage(reader, pageNum);
                processor.processContent(pageContent, resourcesDic);

                String content = listener.getContent();
                List<TextFragment> fragments = listener.getFragments();

                PdfPageContentPositions pageContentPositions = new PdfPageContentPositions();
                pageContentPositions.setContent(content);
                pageContentPositions.setFragments(fragments);

                result.add(pageContentPositions);
            }
            return result;
        } finally {
            reader.close();
        }
    }

    /**
     * 在单页中查找关键字，返回包含关键字的整个文本片段的基线左下角坐标
     */
    private static List<PdfKeywordPosition> findPositionsInPage(String keyword,
                                                                PdfPageContentPositions pageContentPositions, int pageNum) {
        List<PdfKeywordPosition> result = new ArrayList<>();
        String content = pageContentPositions.getContent();
        List<TextFragment> fragments = pageContentPositions.getFragments();

        for (int pos = 0; pos < content.length(); ) {
            int positionIndex = content.indexOf(keyword, pos);
            if (positionIndex == -1) {
                break;
            }

            // 查找包含该起始索引的文本片段
            TextFragment targetFragment = null;
            int currentPos = 0;
            for (TextFragment fragment : fragments) {
                int fragmentStart = currentPos;
                int fragmentEnd = currentPos + fragment.text.length() - 1;
                if (positionIndex >= fragmentStart && positionIndex <= fragmentEnd) {
                    targetFragment = fragment;
                    break;
                }
                currentPos += fragment.text.length();
            }

            if (targetFragment == null) {
                pos = positionIndex + 1;
                continue;
            }

            // 获取该片段的基线矩形左下角坐标
            Rectangle2D.Float rect = targetFragment.baselineRect;
            result.add(new PdfKeywordPosition(pageNum, rect.x, rect.y));
            pos = positionIndex + 1;
        }

        return result;
    }

    /**
     * 文本片段信息
     */
    private static class TextFragment {
        String text;
        Rectangle2D.Float baselineRect;

        TextFragment(String text, Rectangle2D.Float baselineRect) {
            this.text = text;
            this.baselineRect = baselineRect;
        }
    }

    /**
     * 单页内容和文本片段列表
     */
    private static class PdfPageContentPositions {
        private String content;
        private List<TextFragment> fragments;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public List<TextFragment> getFragments() { return fragments; }
        public void setFragments(List<TextFragment> fragments) { this.fragments = fragments; }
    }

    /**
     * 渲染监听器，提取文本片段及其基线矩形
     */
    private static class PdfRenderListener implements RenderListener {
        private static final Logger log = LoggerFactory.getLogger(PdfRenderListener.class);

        private final int pageNum;
        private final StringBuilder contentBuilder = new StringBuilder();
        private final List<TextFragment> fragments = new ArrayList<>();

        public PdfRenderListener(int pageNum) {
            this.pageNum = pageNum;
        }

        @Override
        public void beginTextBlock() { }

        @Override
        public void renderText(TextRenderInfo renderInfo) {
            String text = renderInfo.getText();
            if (text == null || text.isEmpty()) {
                return;
            }

            try {
                Rectangle2D.Float rect = renderInfo.getBaseline().getBoundingRectange();
                fragments.add(new TextFragment(text, rect));
                contentBuilder.append(text);
            } catch (Exception e) {
                log.error("第 {} 页 - 处理文本片段时发生异常", pageNum, e);
            }
        }

        @Override
        public void endTextBlock() { }

        @Override
        public void renderImage(ImageRenderInfo renderInfo) { }

        public String getContent() {
            return contentBuilder.toString();
        }

        public List<TextFragment> getFragments() {
            return fragments;
        }
    }
}