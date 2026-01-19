package net.doge.util.common;

import cn.hutool.core.util.StrUtil;

/**
 * @Author Doge
 * @Description 数字工具类
 * @Date 2020/12/15
 */
public class NumUtil {
    /**
     * 数字位数
     *
     * @param n
     * @return
     */
    public static int bit(int n) {
        int l = String.valueOf(n).length();
        return n < 0 ? l - 1 : l;
    }

    /**
     * 判断字符串是否为纯数字
     *
     * @param s
     * @return
     */
    public static boolean isNumber(String s) {
        return StrUtil.isNumeric(s);
    }

    /**
     * 转为数字
     *
     * @param s
     * @return
     */
    public static int toNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
