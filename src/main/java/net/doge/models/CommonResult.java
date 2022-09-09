package net.doge.models;

import java.util.List;

public class CommonResult<E> {
    public List<E> data;
    public Integer total;

    public CommonResult(List<E> data, Integer total) {
        this.data = data;
        this.total = total;
    }
}
