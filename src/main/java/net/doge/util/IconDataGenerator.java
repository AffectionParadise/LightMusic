package net.doge.util;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.system.SimplePath;
import net.doge.constant.ui.ImageConstants;
import net.doge.util.system.FileUtil;
import net.doge.util.system.LMDataUtil;
import net.doge.util.ui.ImageUtil;

import java.io.File;

/**
 * 生成 icon 数据文件
 */
public class IconDataGenerator {
    private static JSONObject data = new JSONObject();
    private static String prefix;

    public static void main(String[] args) {
        File iconDir = new File(SimplePath.ICON_PATH);
        walk(iconDir, false);
        File iconData = new File(SimplePath.ICON_PATH + ImageConstants.ICON_DATA_FILE_NAME);
        iconData.delete();
        LMDataUtil.toFile(data, iconData);
    }

    public static void walk(File dir, boolean inner) {
        prefix = inner ? dir.getName() + "." : "";
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) walk(f, true);
            else {
                try {
                    data.put(prefix + FileUtil.getPrefix(f), ImageUtil.toBase64(ImageUtil.read(f)));
                } catch (Exception e) {

                }
            }
        }
        prefix = "";
    }
}
