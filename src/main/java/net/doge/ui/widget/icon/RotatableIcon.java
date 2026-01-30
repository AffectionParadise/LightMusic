//package net.doge.ui.widget.icon;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.geom.AffineTransform;
//import java.awt.image.BufferedImage;
//
/// **
// * 可旋转图标
// */
//public class RotatableIcon implements Icon {
//    private BufferedImage img;
//    @Getter
//    @Setter
//    private double angle;
//
//    public RotatableIcon(BufferedImage img) {
//        this.img = img;
//    }
//
//    // 效果一般，且会发生未知偏移
//    @Override
//    public void paintIcon(Component c, Graphics g, int x, int y) {
//        Graphics2D g2d = GraphicsUtil.setup(g);
//        // 计算旋转中心（图标的中心）
//        int centerX = x + getIconWidth() / 2, centerY = y + getIconHeight() / 2;
//        // 旋转
//        AffineTransform originalTransform = g2d.getTransform();
//        AffineTransform transform = AffineTransform.getRotateInstance(angle, centerX, centerY);
//        g2d.setTransform(transform);
//        // 绘制图标
//        g2d.drawImage(img, x, y, c);
//        g2d.setTransform(originalTransform);
//    }
//
//    @Override
//    public int getIconWidth() {
//        return img.getWidth();
//    }
//
//    @Override
//    public int getIconHeight() {
//        return img.getHeight();
//    }
//}
