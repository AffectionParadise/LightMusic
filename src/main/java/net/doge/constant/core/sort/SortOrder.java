package net.doge.constant.core.sort;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Doge
 * @Description 排序方式
 * @Date 2020/12/11
 */
@Getter
@AllArgsConstructor
public enum SortOrder {
    // 升序
    ASCENDING(0),
    // 降序
    DESCENDING(1);

    private int index;

    public static SortOrder fromIndex(int index) {
        for (SortOrder sortOrder : values()) {
            if (sortOrder.index == index) return sortOrder;
        }
        return null;
    }
}
