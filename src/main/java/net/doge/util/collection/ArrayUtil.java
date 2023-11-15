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
     * @param ch
     * @return
     */
    public static boolean in(char[] chars, char ch) {
        return cn.hutool.core.util.ArrayUtil.indexOf(chars, ch) > -1;
    }

    /**
     * 删除数组中所有空值
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T extends CharSequence> T[] removeEmpty(T[] array) {
        return cn.hutool.core.util.ArrayUtil.removeEmpty(array);
    }

    /**
     * 返回元素在数组中的位置
     *
     * @param array
     * @param val
     * @param <T>
     * @return
     */
    public static <T> int indexOf(T[] array, T val) {
        return cn.hutool.core.util.ArrayUtil.indexOf(array, val);
    }

    /**
     * 返回子数组在原数组中的位置
     *
     * @param array
     * @param subArray
     * @return
     */
    public static int indexOf(byte[] array, byte[] subArray) {
        int[] next = calculateNext(subArray);
        int i = 0;
        int j = 0;
        while (i < array.length && j < subArray.length) {
            if (array[i] == subArray[j]) {
                i++;
                j++;
            } else {
                if (j > 0) j = next[j - 1];
                else i++;
            }
        }
        if (j == subArray.length) return i - j;
        return -1;
    }

    private static int[] calculateNext(byte[] subArray) {
        int[] next = new int[subArray.length];
        int i = 1;
        int j = 0;
        while (i < subArray.length) {
            if (subArray[i] == subArray[j]) {
                next[i] = j + 1;
                i++;
                j++;
            } else if (j > 0) {
                j = next[j - 1];
            } else {
                next[i] = 0;
                i++;
            }
        }
        return next;
    }

//    /**
//     * 原地反转数组，返回原数组
//     *
//     * @param longs
//     */
//    public static long[] reverse(long[] longs) {
//        return cn.hutool.core.util.ArrayUtil.reverse(longs);
//    }

    /**
     * 原地反转数组，返回原数组
     *
     * @param bytes
     */
    public static byte[] reverse(byte[] bytes) {
        return cn.hutool.core.util.ArrayUtil.reverse(bytes);
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
     * byte 数组转为 int 数组
     *
     * @param bytes
     * @return
     */
    public static int[] bytesToInts(byte[] bytes) {
        int n = bytes.length;
        int[] ints = new int[n];
        for (int i = 0; i < n; i++) ints[i] = bytes[i] & 0xFF;
        return ints;
    }

    /**
     * int 数组转为 byte 数组
     *
     * @param ints
     * @return
     */
    public static byte[] intsToBytes(int[] ints) {
        int n = ints.length;
        byte[] bytes = new byte[n];
        for (int i = 0; i < n; i++) bytes[i] = (byte) ints[i];
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
