package com.agile.pdf.core.model;

/**
 * PDF 关键字位置信息
 */
public class PdfKeywordPosition {
    private final int pageNum;
    private final float x;
    private final float y;

    public PdfKeywordPosition(int pageNum, float x, float y) {
        this.pageNum = pageNum;
        this.x = x;
        this.y = y;
    }

    public int getPageNum() {
        return pageNum;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "PdfKeywordPosition{" +
                "pageNum=" + pageNum +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}