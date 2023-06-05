package net.doge.utils;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/15
 */
public class ArrayUtil {
    /**
     * 判断数据是否在数组
     *
     * @param chars
     * @param c
     * @return
     */
    public static boolean inArray(char[] chars, char c) {
        for (char ch : chars) {
            if (ch == c) return true;
        }
        return false;
    }
}
