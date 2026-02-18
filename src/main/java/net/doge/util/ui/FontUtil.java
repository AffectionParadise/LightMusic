package net.doge.util.ui;

import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.log.LogUtil;

import java.awt.*;
import java.io.ByteArrayInputStream;

/**
 * @author Doge
 * @description 字体工具类
 * @date 2020/12/15
 */
public class FontUtil {
    /**
     * base64 转为字体
     *
     * @param base64
     * @param fontSize
     * @return
     */
    public static Font toFont(String base64, float fontSize) {
        try {
            byte[] bytes = CryptoUtil.base64DecodeToBytes(base64);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, in);
            Font dynamicFontPt = dynamicFont.deriveFont(fontSize);
            in.close();
            // 注册该字体以便 HTML 调用
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(dynamicFontPt);
            return dynamicFontPt;
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }
}