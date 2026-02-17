package net.doge.sdk.common.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * 分页请求数据封装
 *
 * @param <E>
 */
public class CommonResult<E> {
    public List<E> data;
    public Integer total;
    public String cursor;

    public CommonResult(List<E> data, Integer total) {
        this(data, total, "");
    }

    public CommonResult(List<E> data, Integer total, String cursor) {
        this.data = data;
        this.total = total;
        this.cursor = cursor;
    }

    // 构造空的请求数据
    public static <E> CommonResult<E> create() {
        return new CommonResult<>(new LinkedList<>(), 0);
    }
}
