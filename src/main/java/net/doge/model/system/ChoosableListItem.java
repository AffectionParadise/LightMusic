package net.doge.model.system;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用于 JList 中显示可复选的元素
 *
 * @param <I>
 */

@Data
@AllArgsConstructor
public class ChoosableListItem<I> {
    private I item;
    private boolean selected;

    public ChoosableListItem(I item) {
        this.item = item;
    }
}
