package net.doge.constant.core.os;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author Doge
 * @description
 * @date 2020/12/9
 */
public class SimplePath {
    private static final String SEPARATOR = File.separator;

    private static String buildPath(String... parts) {
        return String.join(SEPARATOR, parts);
    }

    // 插件路径
    public static String PLUGIN_PATH = buildPath("plugin");
    // 临时路径
    public static String TEMP_PATH = System.getProperty("java.io.tmpdir");

    // 资源路径
    public static String RESOURCE_PATH = buildPath("resource");
    // 自定义主题图片
    public static String CUSTOM_STYLE_PATH = buildPath(RESOURCE_PATH, "customStyle");

    // 缓存路径
    public static String CACHE_PATH = buildPath("cache");
    // 图片缓存路径
    public static String IMG_CACHE_PATH = buildPath(CACHE_PATH, "img");
    // 下载路径
    public static String DOWNLOAD_PATH = buildPath("download");
    // 下载的音乐路径
    public static String DOWNLOAD_MUSIC_PATH = buildPath(DOWNLOAD_PATH, "music");
    // 下载的 MV 路径
    public static String DOWNLOAD_MV_PATH = buildPath(DOWNLOAD_PATH, "mv");

    static {
        try {
            // 所有路径后面添加分隔符
            Field[] fields = SimplePath.class.getFields();
            for (Field field : fields) {
                String path = (String) field.get(null);
                if (path.endsWith(SEPARATOR)) continue;
                field.set(null, path + SEPARATOR);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
