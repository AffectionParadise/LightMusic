//package net.doge.util.ui;
//
//import java.awt.*;
//
/// **
// * @Author Doge
// * @Description 缩放工具类
// * @Date 2020/12/15
// */
//public class ScaleUtil {
//    public static final float SCALE = Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f;
//
//    /**
//     * 缩放 Dimension 实例
//     *
//     * @param dimension
//     * @return
//     */
//    public static Dimension scale(Dimension dimension) {
//        if (dimension == null) return null;
//        return new Dimension((int) (dimension.width * SCALE), (int) (dimension.height * SCALE));
//    }
//
//    public static Dimension unscale(Dimension dimension) {
//        if (dimension == null) return null;
//        return new Dimension((int) (dimension.width / SCALE), (int) (dimension.height / SCALE));
//    }
//
//    /**
//     * 缩放一个值
//     * @param value
//     * @return
//     */
//    public static int scale(int value) {
//        return (int) (value * SCALE);
//    }
//
//    public static Graphics2D scale(Graphics g) {
//        Graphics2D g2d = (Graphics2D) g;
//        g2d.scale(1.02, 1.02);
//        return g2d;
//    }
//}