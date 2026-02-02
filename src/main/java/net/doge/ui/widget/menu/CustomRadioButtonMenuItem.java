package net.doge.ui.widget.menu;

/**
 * @Author Doge
 * @Description 单选菜单项自定义 UI
 * @Date 2020/12/13
 */
public class CustomRadioButtonMenuItem extends CustomMenuItem {
    //    private boolean drawBg;
//    private Timer drawBgTimer;
//    private float alpha;
//    private final float destAlpha = 0.1f;
//
    public CustomRadioButtonMenuItem(String text) {
        super(text);
    }
//
//    public CustomRadioButtonMenuItem(String text, boolean selected) {
//        super(text, selected);
//        init();
//    }
//
//    private void init() {
//        setFont(Fonts.NORMAL);
//        createBorder();
//        initResponse();
//    }
//
//    private void initResponse() {
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                setDrawBg(true);
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                setDrawBg(false);
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                setDrawBg(false);
//            }
//        });
//
//        addMouseWheelListener(e -> setDrawBg(false));
//
//        drawBgTimer = new Timer(2, e -> {
//            if (drawBg) alpha = Math.min(destAlpha, alpha + 0.005f);
//            else alpha = Math.max(0f, alpha - 0.005f);
//            if (alpha <= 0f || alpha >= destAlpha) drawBgTimer.stop();
//            repaint();
//        });
//    }
//
//    private void setDrawBg(boolean drawBg) {
//        if (this.drawBg == drawBg) return;
//        this.drawBg = drawBg;
//        if (drawBgTimer.isRunning()) return;
//        drawBgTimer.start();
//    }
//
//    private void createBorder() {
//        setBorder(new HDEmptyBorder(4, 5, 4, 0));
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        // 画背景
//        Graphics2D g2d = GraphicsUtil.setup(g);
//        g2d.setColor(getForeground());
//        GraphicsUtil.srcOver(g2d, alpha);
//        int arc = ScaleUtil.scale(10);
//        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
//        GraphicsUtil.srcOver(g2d);
//
//        super.paintComponent(g);
//    }
}
