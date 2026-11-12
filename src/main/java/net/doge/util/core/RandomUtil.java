package net.doge.util.core;

import cn.hutool.core.util.IdUtil;

import java.util.Random;
import java.util.UUID;

/**
 * @author Doge
 * @description 随机工具类
 * @date 2020/12/15
 */
public class RandomUtil {
    private static final Random random = new Random();

    /**
     * 生成随机 [0, limit) 整数
     *
     * @return
     */
    public static int randomInt(int limitExclude) {
        return cn.hutool.core.util.RandomUtil.randomInt(limitExclude);
    }

    /**
     * 生成随机 [min, max) 整数
     *
     * @return
     */
    public static int randomInt(int minInclude, int maxExclude) {
        return cn.hutool.core.util.RandomUtil.randomInt(minInclude, maxExclude);
    }

    /**
     * 生成随机 [0, max) 双浮点
     *
     * @return
     */
    public static double randomDouble(double maxExclude) {
        return cn.hutool.core.util.RandomUtil.randomDouble(maxExclude);
    }

    /**
     * 生成随机 Ipv4 地址
     *
     * @return
     */
    public static String randomIpv4() {
        return String.format("%d.%d.%d.%d", random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * 生成随机 4 类型的 UUID
     *
     * @return
     */
    public static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成随机 32 位不带 - 的 GUID
     *
     * @return
     */
    public static String randomGuid() {
        return IdUtil.simpleUUID().toUpperCase();
    }

    /**
     * 生成随机指定位补零数字
     *
     * @return
     */
    public static String randomNumbers(int len) {
        return cn.hutool.core.util.RandomUtil.randomNumbers(len);
    }

    /**
     * 随机选取数组中一个元素
     *
     * @param array
     */
    public static <T> T randomChoose(T[] array) {
        return array[random.nextInt(array.length)];
    }

    /**
     * 随机生成指定位 bytes
     *
     * @param len
     * @return
     */
    public static byte[] randomBytes(int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) bytes[i] = (byte) random.nextInt(128);
        return bytes;
    }
}
