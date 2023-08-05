package net.doge.util.system;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.system.SimplePath;
import net.doge.constant.ui.ImageConstants;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description 图标、主题工具类
 * @Date 2020/12/15
 */
public class LMIconManager {
    private static final JSONObject ICON_DATA = LMDataUtil.read(SimplePath.ICON_PATH + ImageConstants.ICON_DATA_FILE_NAME);

    /**
     * 根据 key 获取 ImageIcon
     *
     * @param key
     * @return
     */
    public static ImageIcon getIcon(String key) {
        try {
            return new ImageIcon(getImage(key));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据 key 获取 BufferedImage
     *
     * @param key
     * @return
     */
    public static BufferedImage getImage(String key) {
        try {
            return ImageUtil.toImage(getIconBase64(key));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据 key 获取图标的 Base64
     *
     * @param key
     * @return
     */
    public static String getIconBase64(String key) {
        return ICON_DATA.getString(key);
    }
}
