package net.doge.constant.ui;

import net.doge.util.system.LMIconManager;

import java.awt.image.BufferedImage;

/**
 * @Author Doge
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

    // 图标数据文件名
    public static final String ICON_DATA_FILE_NAME = "icon.lm";

    // 默认图片
    public static final BufferedImage DEFAULT_IMG = LMIconManager.getImage("default");
}
