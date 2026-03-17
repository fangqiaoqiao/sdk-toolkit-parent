package com.agile.pdf.core.model;

import com.itextpdf.text.BaseColor;

/**
 * PDF 水印信息封装类。
 * 各字段含义：
 * <ul>
 *   <li>text          - 水印文字</li>
 *   <li>opacity       - 透明度 (0.0 ~ 1.0)</li>
 *   <li>fontSize      - 字体大小（点）</li>
 *   <li>angle         - 旋转角度</li>
 *   <li>heightDensity - 垂直密度因子（步长 = 字体高度 * heightDensity）</li>
 *   <li>widthDensity  - 水平密度因子（步长 = 文字宽度 * widthDensity）</li>
 *   <li>color         - 水印颜色（默认为黑色）</li>
 * </ul>
 */
public class PdfWaterInfo {
    private String text;
    private float opacity;
    private int fontSize;
    private int angle;
    private int heightDensity;
    private int widthDensity;
    private BaseColor color;

    public PdfWaterInfo() {}

    public PdfWaterInfo(String text, float opacity, int fontSize, int angle,
                        int heightDensity, int widthDensity, BaseColor color) {
        this.text = text;
        this.opacity = opacity;
        this.fontSize = fontSize;
        this.angle = angle;
        this.heightDensity = heightDensity;
        this.widthDensity = widthDensity;
        this.color = color;
    }

    // getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public float getOpacity() { return opacity; }
    public void setOpacity(float opacity) { this.opacity = opacity; }

    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }

    public int getAngle() { return angle; }
    public void setAngle(int angle) { this.angle = angle; }

    public int getHeightDensity() { return heightDensity; }
    public void setHeightDensity(int heightDensity) { this.heightDensity = heightDensity; }

    public int getWidthDensity() { return widthDensity; }
    public void setWidthDensity(int widthDensity) { this.widthDensity = widthDensity; }

    public BaseColor getColor() { return color; }
    public void setColor(BaseColor color) { this.color = color; }
}