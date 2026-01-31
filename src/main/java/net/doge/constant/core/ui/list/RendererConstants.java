package net.doge.constant.core.ui.list;

import net.doge.util.ui.ScaleUtil;

/**
 * @Author Doge
 * @Description 列表渲染参数
 * @Date 2020/12/7
 */
public class RendererConstants {
    // 元素宽度
    public static final int CELL_WIDTH = ScaleUtil.scale(180);
    // 文字宽度
    public static final int TEXT_WIDTH = CELL_WIDTH - ScaleUtil.scale(20);
    // 字符串最大长度(超出省略)
    public static final int STRING_MAX_LENGTH = 50;
}
