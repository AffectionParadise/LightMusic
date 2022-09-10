package net.doge.utils;

import cn.hutool.core.collection.ListUtil;
import net.doge.models.NetMusicInfo;

import java.util.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/15
 */
public class ListUtils {

    /**
     * 搜索元素位置，找不到返回 -1
     *
     * @param list
     * @param obj
     * @return
     */
    public static int search(List list, Object obj) {
        for (int i = 0, len = list.size(); i < len; i++) {
            if (list.get(i).equals(obj)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 将 list 中的所有 l 交错合并
     * @param list
     * @return
     * @param <T>
     */
    public static <T> List<T> joinAll(List<List<T>> list) {
        int max = 0;
        for (List<T> l : list) max = Math.max(max, l.size());
        List<T> res = new LinkedList<>();
        for (int i = 0; i < max; i++) {
            for (List<T> l : list) {
                if (i < l.size()) res.add(l.get(i));
            }
        }
        // 去重
        distinct(res);
        return res;
    }

    /**
     * list 原地去重
     * @param list
     */
    public static <T> void distinct(List<T> list) {
        Set<T> set = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(set);
    }
}
