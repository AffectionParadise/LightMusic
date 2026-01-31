package net.doge.ui.widget.dialog.base;

import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.button.CustomButton;
import net.doge.ui.widget.button.listener.CustomButtonMouseListener;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.HtmlUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @Author Doge
 * @Description 抽象含标题对话框
 * @Date 2021/1/5
 */
public abstract class AbstractTitledDialog extends AbstractShadowDialog {
    protected CustomPanel topPanel = new CustomPanel();
    protected CustomLabel titleLabel = new CustomLabel();
    private CustomPanel windowCtrlPanel = new CustomPanel();
    protected CustomButton closeButton = new CustomButton();
    private String title;

    public AbstractTitledDialog(MainFrame f, String title) {
        super(f);
        this.title = title;
    }

    // 初始化标题栏
    protected void initTitleBar() {
        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        topPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        topPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // mouseDragged 不能正确返回 button 值，需要借助此方法
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
            }
        });

        titleLabel.setFont(Fonts.NORMAL_TITLE2);
        titleLabel.setText(title);
        setTitle(HtmlUtil.removeHtmlLabel(title));
        titleLabel.setForeground(f.currUIStyle.getTextColor());
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setPreferredSize(new HDDimension(600, 30));
        closeButton.setIcon(ImageUtil.dye(f.closeWindowIcon, f.currUIStyle.getIconColor()));
        closeButton.setPreferredSize(new Dimension(f.closeWindowIcon.getIconWidth() + ScaleUtil.scale(10), f.closeWindowIcon.getIconHeight() + ScaleUtil.scale(10)));
        // 关闭窗口
        closeButton.addActionListener(e -> close());
        // 鼠标事件
        closeButton.addMouseListener(new CustomButtonMouseListener(closeButton, f));
        FlowLayout fl = new HDFlowLayout(HDFlowLayout.RIGHT);
        windowCtrlPanel.setLayout(fl);
        windowCtrlPanel.setMinimumSize(new HDDimension(40, 30));
        windowCtrlPanel.add(closeButton);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(windowCtrlPanel);
        topPanel.setBorder(new HDEmptyBorder(5, 15, 0, 15));
        globalPanel.add(topPanel, BorderLayout.NORTH);
    }

    protected void close() {
        f.currDialogs.remove(this);
        dispose();
    }
}