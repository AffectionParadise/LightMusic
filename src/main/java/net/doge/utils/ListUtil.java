package net.doge.utils;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/15
 */
public class ListUtil {

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
     * 判断两个 list 是否相等
     *
     * @param list1
     * @param list2
     * @return
     */
    public static boolean equals(List list1, List list2) {
        if (list1 == list2) return true;
        if (list1 == null && list2 != null || list1 != null && list2 == null) return false;
        if (list1.size() != list2.size()) return false;
        if (!list1.containsAll(list2)) return false;
        return true;
    }

    /**
     * 将 list 中的所有 l 交错合并
     *
     * @param list
     * @param <T>
     * @return
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
     *
     * @param list
     */
    public static <T> void distinct(List<T> list) {
        Set<T> set = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(set);
    }
}
