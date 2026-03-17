package com.agile.common.font;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字体加载器，支持缓存和降级。
 * 使用方式：
 *   Font font = FontLoader.getFont("/path/to/font.ttc", 12f); // 指定大小
 *   Font baseFont = FontLoader.getBaseFont("/path/to/font.ttc"); // 获取原始字体（未调整大小）
 */
public class FontLoader {

    private static final Logger log = LoggerFactory.getLogger(FontLoader.class);

    // 默认字体（当字体文件加载失败或未指定时使用）
    private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 18);

    // 字体缓存：路径 -> Font 对象（原始大小，未 derive）
    private static final Map<String, Font> FONT_CACHE = new ConcurrentHashMap<>();

    private FontLoader() {}

    /**
     * 获取字体（带大小调整），若 fontPath 为空或加载失败，返回默认字体。
     *
     * @param fontPath 字体文件路径，为空时返回默认字体
     * @param size     所需字体大小
     * @return Font 对象
     */
    public static Font getFont(String fontPath, float size) {
        Font base = getBaseFont(fontPath);
        return base.deriveFont(size);
    }

    /**
     * 获取原始字体（未调整大小），若 fontPath 为空或加载失败，返回默认字体。
     */
    public static Font getBaseFont(String fontPath) {
        if (fontPath == null || fontPath.trim().isEmpty()) {
            return DEFAULT_FONT;
        }
        return FONT_CACHE.computeIfAbsent(fontPath, path -> {
            try {
                byte[] fontData = Files.readAllBytes(Paths.get(path));
                Font font = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(fontData));
                log.info("字体加载成功: {}", path);
                return font;
            } catch (Exception e) {
                log.error("字体加载失败，使用默认字体: {}", path, e);
                return DEFAULT_FONT;
            }
        });
    }

    /**
     * 清空缓存（通常不需要，但可提供用于测试）
     */
    public static void clearCache() {
        FONT_CACHE.clear();
    }
}