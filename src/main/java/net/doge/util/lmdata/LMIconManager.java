package net.doge.util.lmdata;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.LMDataConstants;
import net.doge.constant.core.SimplePath;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description 图标管理器
 * @Date 2020/12/15
 */
public class LMIconManager {
    private static final JSONObject ICON_DATA = LMDataUtil.read(SimplePath.RESOURCE_PATH + LMDataConstants.ICON_DATA_FILE_NAME);

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
        return ImageUtil.toImage(getBase64(key));
    }

    /**
     * 根据 key 获取图标的 Base64
     *
     * @param key
     * @return
     */
    public static String getBase64(String key) {
        return ICON_DATA.getString(key);
    }
}
