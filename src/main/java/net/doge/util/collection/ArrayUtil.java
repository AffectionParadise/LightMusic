package net.doge.util.collection;

import java.util.Random;

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

    /**
     * 反转数组
     *
     * @param bytes
     */
    public static void reverse(byte[] bytes) {
        for (int i = 0, len = bytes.length; i < len / 2; i++) {
            byte t = bytes[i];
            bytes[i] = bytes[len - i - 1];
            bytes[len - i - 1] = t;
        }
    }

    /**
     * 随机选取数组中一个元素
     *
     * @param array
     */
    public static <T> T randomChoose(T[] array) {
        Random rand = new Random();
        int num = rand.nextInt(array.length);
        return array[num];
    }

    /**
     * 随机生成指定位 bytes
     *
     * @param n
     * @return
     */
    public static byte[] randomBytes(int n) {
        byte[] bytes = new byte[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) bytes[i] = (byte) random.nextInt(128);
        return bytes;
    }

    /**
     * 连接多个数组
     *
     * @param arrays
     * @param <T>
     * @return
     */
    public static <T> T[] concat(T[]... arrays) {
        return cn.hutool.core.util.ArrayUtil.addAll(arrays);
    }
}
