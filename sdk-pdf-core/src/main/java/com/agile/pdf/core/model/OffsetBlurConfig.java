package com.agile.pdf.core.model;

/**
 * 偏移模糊配置（相对于关键字坐标的偏移量）
 */
public class OffsetBlurConfig {
    private final String coverImagePath;
    private final float scaleWidthPercent;
    private final float scaleHeightPercent;
    private final float xOffset;
    private final float yOffset;

    /**
     * 全参构造器
     *
     * @param coverImagePath     覆盖图片路径
     * @param scaleWidthPercent  图片宽度缩放百分比
     * @param scaleHeightPercent 图片高度缩放百分比
     * @param xOffset            X 轴偏移量（相对关键字坐标）
     * @param yOffset            Y 轴偏移量（相对关键字坐标）
     */
    public OffsetBlurConfig(String coverImagePath,
                            float scaleWidthPercent,
                            float scaleHeightPercent,
                            float xOffset,
                            float yOffset) {
        this.coverImagePath = coverImagePath;
        this.scaleWidthPercent = scaleWidthPercent;
        this.scaleHeightPercent = scaleHeightPercent;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public float getScaleWidthPercent() {
        return scaleWidthPercent;
    }

    public float getScaleHeightPercent() {
        return scaleHeightPercent;
    }

    public float getXOffset() {
        return xOffset;
    }

    public float getYOffset() {
        return yOffset;
    }
}