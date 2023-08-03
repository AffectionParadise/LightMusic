package net.doge.constant.system;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/9
 */
public class SimplePath {
    private static final String SEPARATOR = File.separator;

    // 临时路径
    public static String TEMP_PATH = System.getProperty("java.io.tmpdir");

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
