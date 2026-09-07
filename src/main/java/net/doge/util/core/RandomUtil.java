package net.doge.util.core;

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
     * 生成随机指定位数字
     *
     * @return
     */
    public static String randomNumbers(int n) {
        return cn.hutool.core.util.RandomUtil.randomNumbers(n);
    }

    /**
     * 随机选取数组中一个元素
     *
     * @param array
     */
    public static <T> T randomChoose(T[] array) {
        int num = random.nextInt(array.length);
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
        for (int i = 0; i < n; i++) bytes[i] = (byte) random.nextInt(128);
        return bytes;
    }
}
