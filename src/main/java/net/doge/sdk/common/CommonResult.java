package net.doge.sdk.common;

import java.util.List;

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
