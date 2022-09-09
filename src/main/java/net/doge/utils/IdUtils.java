package net.doge.utils;

import java.util.UUID;

/**
 * @Author yzx
 * @Description 生成随机不重复 id
 * @Date 2020/12/15
 */
public class IdUtils {

    /**
     * 生成随机不重复 id，返回 String
     * @return
     */
    public static String randomId() {
        return UUID.randomUUID().toString();
    }
}
