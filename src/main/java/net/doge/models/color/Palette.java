package net.doge.models.color;

import java.awt.*;

/**
 * 调色盘
 */
public class Palette {
    public Color vibrant;
    public Color lightVibrant;
    public Color darkVibrant;
    public Color muted;
    public Color lightMuted;
    public Color darkMuted;

    @Override
    public String toString() {
        return "Palette{" +
                "vibrant=" + vibrant +
                ", lightVibrant=" + lightVibrant +
                ", darkVibrant=" + darkVibrant +
                ", muted=" + muted +
                ", lightMuted=" + lightMuted +
                ", darkMuted=" + darkMuted +
                '}';
    }
}
