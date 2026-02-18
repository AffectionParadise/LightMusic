package net.doge.util.lmdata.manager;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.LMDataConstants;
import net.doge.constant.core.os.SimplePath;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.lmdata.LMDataUtil;

import java.awt.image.BufferedImage;

/**
 * @author Doge
 * @description 主题管理器
 * @date 2020/12/15
 */
public class LMStyleManager {
    private static final JSONObject STYLE_DATA = LMDataUtil.readOrCreate(SimplePath.RESOURCE_PATH + LMDataConstants.STYLE_DATA_FILE_NAME);

    /**
     * 根据 key 获取 BufferedImage
     *
     * @param key
     * @return
     */
    public static BufferedImage getImage(String key) {
        return ImageUtil.toImage(STYLE_DATA.getString(key));
    }

    /**
     * 判断是否存在 imgKey
     *
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        return STYLE_DATA.containsKey(key);
    }
}
