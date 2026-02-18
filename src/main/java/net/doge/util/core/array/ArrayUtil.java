package net.doge.util.core.array;

import net.doge.util.core.StringUtil;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Doge
 * @description
 * @date 2020/12/15
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
     * 删除字符串数组中第一个位置上的空值
     *
     * @param array
     * @return
     */
    public static String[] removeFirstEmpty(String[] array) {
        int len = array.length;
        if (len == 0 || StringUtil.notEmpty(array[0])) return array;
        return Arrays.copyOfRange(array, 1, len);
    }

    /**
     * 删除字符串数组中最后一个位置上的空值
     *
     * @param array
     * @return
     */
    public static String[] removeLastEmpty(String[] array) {
        int len = array.length;
        if (len == 0 || StringUtil.notEmpty(array[len - 1])) return array;
        return Arrays.copyOfRange(array, 0, len - 1);
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
        int i = 0, j = 0, len = array.length, sLen = subArray.length;
        while (i < len && j < sLen) {
            if (array[i] == subArray[j]) {
                i++;
                j++;
            } else {
                if (j > 0) j = next[j - 1];
                else i++;
            }
        }
        if (j == sLen) return i - j;
        return -1;
    }

    private static int[] calculateNext(byte[] array) {
        int i = 1, j = 0, len = array.length;
        int[] next = new int[len];
        while (i < len) {
            if (array[i] == array[j]) {
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
     * byte 数组转为 short 数组
     *
     * @param bytes
     * @return
     */
    public static short[] bytesToShorts(byte[] bytes) {
        int n = bytes.length;
        short[] shorts = new short[n];
        for (int i = 0; i < n; i++) shorts[i] = (short) (bytes[i] & 0xFF);
        return shorts;
    }

    /**
     * short 数组转为 byte 数组
     *
     * @param shorts
     * @return
     */
    public static byte[] shortsToBytes(short[] shorts) {
        int n = shorts.length;
        byte[] bytes = new byte[n];
        for (int i = 0; i < n; i++) bytes[i] = (byte) shorts[i];
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
