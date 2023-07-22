package net.doge.util.collection;

/**
 * @Author Doge
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

    /**
     * 反转数组
     *
     * @param longs
     */
    public static void reverse(long[] longs) {
        for (int i = 0, len = longs.length; i < len / 2; i++) {
            long t = longs[i];
            longs[i] = longs[len - i - 1];
            longs[len - i - 1] = t;
        }
    }
}
