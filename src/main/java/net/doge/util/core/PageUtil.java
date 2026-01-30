package net.doge.util.core;

/**
 * @Author Doge
 * @Description 分页工具类
 * @Date 2020/12/15
 */
public class PageUtil {
    /**
     * 获取总页数
     *
     * @param total
     * @param limit
     * @return
     */
    public static int totalPage(int total, int limit) {
        if (limit <= 0) return 0;
        return total % limit == 0 ? total / limit : total / limit + 1;
    }

    /**
     * 获取总页数(至少返回一页)
     *
     * @param total
     * @param limit
     * @return
     */
    public static int totalPageAtLeastOne(int total, int limit) {
        return Math.max(totalPage(total, limit), 1);
    }
}
