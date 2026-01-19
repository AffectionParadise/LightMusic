package net.doge.util;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.LMDataConstants;
import net.doge.constant.core.SimplePath;
import net.doge.util.common.CryptoUtil;
import net.doge.util.lmdata.LMDataUtil;
import net.doge.util.os.FileUtil;

import java.io.File;

/**
 * 生成 icon 数据文件
 */
public class LMDataGenerator {
    private static JSONObject data;
    private static String prefix;

    public static void main(String[] args) {
        icon();
//        style();
//        font();
    }

    static void icon() {
        data = new JSONObject();
        File iconDir = new File("icon");
        walk(iconDir, false, true);
        File iconData = new File(SimplePath.RESOURCE_PATH + LMDataConstants.ICON_DATA_FILE_NAME);
        iconData.delete();
        LMDataUtil.toFile(data, iconData);
    }

    static void style() {
        data = new JSONObject();
        File dir = new File("style");
        walk(dir, false, false);
        File styleData = new File(SimplePath.RESOURCE_PATH + LMDataConstants.STYLE_DATA_FILE_NAME);
        styleData.delete();
        LMDataUtil.toFile(data, styleData);
    }

    static void font() {
        data = new JSONObject();
        File dir = new File("font");
        walk(dir, false, false);
        File fontData = new File(SimplePath.RESOURCE_PATH + LMDataConstants.FONT_DATA_FILE_NAME);
        fontData.delete();
        LMDataUtil.toFile(data, fontData);
    }

    public static void walk(File dir, boolean inner, boolean deeper) {
        prefix = inner ? dir.getName() + "." : "";
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                if (deeper) walk(f, true, deeper);
            } else {
                try {
                    data.put(prefix + FileUtil.getPrefix(f), CryptoUtil.base64Encode(FileUtil.readBytes(f)));
                } catch (Exception e) {

                }
            }
        }
        prefix = "";
    }
}
