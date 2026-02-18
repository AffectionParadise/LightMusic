package net.doge.util.core.collection;

import java.util.*;

/**
 * @author Doge
 * @description
 * @date 2020/12/15
 */
public class ListUtil {
    /**
     * 创建带多个元素的 list
     *
     * @param elements
     * @param <T>
     * @return
     */
    public static <T> List<T> of(T... elements) {
        List<T> list = new LinkedList<>();
        Collections.addAll(list, elements);
        return list;
    }

    /**
     * 判断 list 是否不为空
     *
     * @param list
     * @return
     */
    public static <T> boolean notEmpty(List<T> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * 查找元素位置，找不到返回 -1
     *
     * @param list
     * @param obj
     * @return
     */
    public static <T> int indexOf(List<T> list, Object obj) {
        for (int i = 0, len = list.size(); i < len; i++) {
            if (list.get(i).equals(obj)) return i;
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
    public static <T> boolean equals(List<T> list1, List<T> list2) {
        if (list1 == list2) return true;
        if (list1 == null || list2 == null) return false;
        if (list1.size() != list2.size()) return false;
        return new HashSet<>(list1).containsAll(list2);
    }

    /**
     * list 元素范围求和
     *
     * @param numList
     * @param start
     * @param end
     * @return
     */
    public static int rangeSum(List<Integer> numList, int start, int end) {
        int sum = 0;
        for (int i = start; i < end; i++) sum += numList.get(i);
        return sum;
    }

    /**
     * 二分法查找 list，如果没有，返回左边的元素
     *
     * @param numList
     * @param target
     * @return
     */
    public static int biSearchLeft(List<Integer> numList, int target) {
        int left = 0, right = numList.size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (numList.get(mid) == target) return mid;
            else if (numList.get(mid) > target) right = mid - 1;
            else left = mid + 1;
        }
        return left - 1;
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
        for (int i = 0; i < max; i++)
            for (List<T> l : list)
                if (i < l.size()) res.add(l.get(i));
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
