//package net.doge.ui.widget.textfield.ui;
//
//import net.doge.util.ui.GraphicsUtil;
//
//import javax.swing.*;
//import javax.swing.plaf.basic.BasicTextFieldUI;
//import java.awt.*;
//
/// **
// * @Author Doge
// * @Description 文本框自定义 UI
// * @Date 2020/12/13
// */
//public class TextFieldUI extends BasicTextFieldUI {
//    private JTextField textField;
//    private Color backgroundColor;
//
//    public TextFieldUI(JTextField textField, Color backgroundColor) {
//        this.textField = textField;
//        this.backgroundColor = backgroundColor;
//    }
//
//    @Override
//    protected void paintBackground(Graphics g) {
//        Graphics2D g2d = GraphicsUtil.setup(g);
//        g2d.setColor(backgroundColor);
//        int arc = ScaleUtil.scale(25);
//        g2d.fillRoundRect(0, 0, textField.getWidth(), textField.getHeight(), arc, arc);
//    }
//}
