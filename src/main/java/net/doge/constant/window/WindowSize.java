package net.doge.constant.window;

public class WindowSize {
    public static int[][] dimensions = {
            {1182, 748},
            {1245, 788},
            {1307, 827},
            {1369, 866},
            {1619, 1024},
            {1960, 1182},
    };
    public static int[][] videoDimensions = {
            {1140, 641},
            {1200, 675},
            {1260, 709},
            {1320, 743},
            {1560, 878},
            {1800, 1013},
    };

    public static int SMALL = 0;
    public static int MIDDLE = 1;
    public static int LARGE = 2;
    public static int LARGER = 3;
    public static int SUPER = 4;
    public static int HUGE = 5;

    public static final String[] names = {"小 (95%)", "中 (100%)", "大 (105%)", "较大 (110%)", "超大 (130%)", "巨大 (150%)"};
}
