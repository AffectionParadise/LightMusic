package net.doge.sdk.common.entity;

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
}
