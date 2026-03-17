package com.agile.pdf.core.model;

/**
 * 固定位置模糊配置（使用绝对坐标）
 */
public class FixedBlurConfig {

    private final String coverImagePath;
    private final float x;
    private final float y;
    private final float scaleWidthPercent;
    private final float scaleHeightPercent;

    /**
     * 全参构造器
     */
    public FixedBlurConfig(String coverImagePath,float x, float y, float scaleWidthPercent, float scaleHeightPercent) {
        this.coverImagePath = coverImagePath;
        this.x = x;
        this.y = y;
        this.scaleWidthPercent = scaleWidthPercent;
        this.scaleHeightPercent = scaleHeightPercent;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getScaleWidthPercent() {
        return scaleWidthPercent;
    }

    public float getScaleHeightPercent() {
        return scaleHeightPercent;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }
}