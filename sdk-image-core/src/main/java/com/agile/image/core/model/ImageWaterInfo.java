package com.agile.image.core.model;

import java.awt.*;

/**
 * 水印信息封装类。
 * 各字段含义：
 * <ul>
 *   <li>text   - 水印文字</li>
 *   <li>opacity - 透明度 (0.0 ~ 1.0)</li>
 *   <li>degree  - 旋转角度 (可为 null，表示不旋转)</li>
 *   <li>x       - 横向偏移/步长/坐标 (具体含义因水印类型而异)</li>
 *   <li>y       - 纵向偏移/步长/坐标</li>
 *   <li>color   - 水印颜色</li>
 *   <li>font    - 水印字体 (若为 null，则使用全局默认字体)</li>
 * </ul>
 */
public class ImageWaterInfo {

    private String text;

    private float opacity;

    private Integer degree;

    private int x;

    private int y;

    private Color color;

    private Font font;

    public ImageWaterInfo() {}

    public ImageWaterInfo(String text, float opacity, Integer degree, int x, int y, Color color, Font font) {
        this.text = text;
        this.opacity = opacity;
        this.degree = degree;
        this.x = x;
        this.y = y;
        this.color = color;
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }
}