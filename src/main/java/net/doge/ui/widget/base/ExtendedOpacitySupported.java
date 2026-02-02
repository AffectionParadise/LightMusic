package net.doge.ui.widget.base;

/**
 * 支持可传递透明的组件标识
 */
public interface ExtendedOpacitySupported {
    // 设置组件本身透明度
    void setExtendedOpacity(float extendedOpacity);

    // 设置组件树透明度
    default void setTreeExtendedOpacity(float extendedOpacity) {
    }

    float getExtendedOpacity();
}
