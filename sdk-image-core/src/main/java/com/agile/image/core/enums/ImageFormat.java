package com.agile.image.core.enums;

public enum ImageFormat {

    JPG, JPEG, PNG, BMP, GIF;

    public static ImageFormat fromString(String format) {
        if (format == null) return null;
        String upper = format.toUpperCase();
        for (ImageFormat f : values()) {
            if (f.name().equals(upper)) return f;
        }
        return null;
    }

    public boolean isPng() {
        return this == PNG;
    }

}
