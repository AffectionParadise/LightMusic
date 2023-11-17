package net.doge.constant.ui;

import net.doge.constant.lang.I18n;

import javax.swing.*;

/**
 * @Author Doge
 * @Description 歌词对齐方式
 * @Date 2020/12/7
 */
public class LyricAlignment {
    public static int lrcAlignmentIndex = 1;
    public static final int[] VALUES = {
            SwingConstants.LEFT,
            SwingConstants.CENTER,
            SwingConstants.RIGHT
    };
    public static final String[] NAMES = {
            I18n.getText("left"),
            I18n.getText("center"),
            I18n.getText("right")
    };
}
