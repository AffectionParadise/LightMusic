package net.doge.constant.ui;

import net.doge.constant.system.SimplePath;
import net.doge.util.ui.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description 图片参数
 * @Date 2020/12/7
 */
public class ImageConstants {
    // 小图标
    public static final int SMALL_WIDTH = 30;
    // 头像图标
    public static final int PROFILE_WIDTH = 70;
    // 中等图标
    public static final int MEDIUM_WIDTH = 120;
    // MV 封面图标
    public static final int MV_COVER_WIDTH = 150;
    // MV 封面最大高度
    public static final int MV_COVER_MAX_HEIGHT = 110;

    // 默认图片
    public static final BufferedImage DEFAULT_IMG = ImageUtil.read(SimplePath.ICON_PATH + "default.jpg");
}
