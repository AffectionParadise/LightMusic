package net.doge.util.lmdata;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.LMDataConstants;
import net.doge.constant.core.SimplePath;
import net.doge.util.ui.FontUtil;

import java.awt.*;

/**
 * @Author Doge
 * @Description 字体管理器
 * @Date 2020/12/15
 */
public class LMFontManager {
    private static final JSONObject FONT_DATA = LMDataUtil.read(SimplePath.RESOURCE_PATH + LMDataConstants.FONT_DATA_FILE_NAME);

    /**
     * 根据 key 获取 Font
     *
     * @param key
     * @return
     */
    public static Font getFont(String key, float fontSize) {
        return FontUtil.toFont(FONT_DATA.getString(key), fontSize);
    }
}
