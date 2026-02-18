package net.doge.constant.core.ui.image;

import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ScaleUtil;

import java.awt.image.BufferedImage;

/**
 * @author Doge
 * @description 图片参数
 * @date 2020/12/7
 */
public class ImageConstants {
    // 小图标
    public static final int SMALL_WIDTH = ScaleUtil.scale(30);
    // 头像图标
    public static final int PROFILE_WIDTH = ScaleUtil.scale(70);
    // 中等图标
    public static final int MEDIUM_WIDTH = ScaleUtil.scale(120);
    // MV 封面图标
    public static final int MV_COVER_WIDTH = ScaleUtil.scale(150);
    // MV 封面最大高度
    public static final int MV_COVER_MAX_HEIGHT = ScaleUtil.scale(110);

    // 默认图片
    public static final BufferedImage DEFAULT_IMG = LMIconManager.getImage("default");
}
