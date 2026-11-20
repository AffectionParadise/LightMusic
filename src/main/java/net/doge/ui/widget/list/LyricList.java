package net.doge.ui.widget.list;

import net.doge.ui.widget.list.renderer.base.StableCellHeightProvider;
import net.doge.ui.widget.list.renderer.core.LyricListRenderer;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

/**
 * 与 Renderer 解耦、支持 JScrollPane 外部滚动的歌词列表。
 * <p>
 * 可以使用任意常规 ListCellRenderer。放入 JScrollPane 后，列表具有真实的文档高度，
 * 滚动条、鼠标滚轮、视口的 setViewPosition() 和 scrollRectToVisible() 都会滚动真实视口。
 * 弹簧动画只为每一行增加独立的视觉位移，不会替代视口位置。
 * 活动歌词索引 activeIndex 变化时，视口可以平滑跟随，歌词行也会依次产生延迟跟随效果。
 * <p>
 * 选中索引 selectedIndex 只用于 Swing 原生选择状态和 Renderer 绘制，不会改变 activeIndex，
 * 也不会触发自动滚动或弹簧动画。外部视口位置变化仍由视口自身控制，不会被自动跟随逻辑抢回。
 * 不在 JScrollPane 中时，仍支持原有的居中歌词模式。
 * 本组件主要用于几百行以内的歌词列表，不适合数百万行的大型数据列表。
 * 兼容 Java 8+，无第三方依赖。
 */
public class LyricList<E> extends CustomList<E> {
    private static final long serialVersionUID = 2L;
    // 动画物理计算允许的最大单步时间(秒)
    private static final double MAX_STEP = 1.0 / 120.0;
    // 位置接近目标的容差(像素)
    private static final double EPS_Y = 0.15;
    // 速度接近静止的容差(像素/秒)
    private static final double EPS_V = 0.6;

    private final CellRendererPane rendererPane = new CellRendererPane();
    // 动画更新定时器
    private Timer timer;
    // 上一帧的时间戳(纳秒)
    private long previousFrameNanos;

    // 几何尺寸是从已安装的 ListCellRenderer 测量得到的，而不是依据自定义约定
    // 每行的高度
    private int[] heights = new int[0];
    // 每行高度前缀和
    private double[] prefix = new double[1];
    // 第 i 行当前的实际绘制 Y 坐标
    private double[] y = new double[0];
    // 第 i 行当前的运动速度
    private double[] v = new double[0];
    // 第 i 行还需要等待多长时间才开始弹簧运动
    private double[] delay = new double[0];
    // 上次测量 Renderer 时使用的可用宽度
    private int measuredWidth = Integer.MIN_VALUE;
    // 上次处理时的视口高度或普通列表高度
    private int measuredViewportHeight = -1;
    // 布局数据是否需要重新测量
    private boolean metricsDirty = true;
    // 每行的 y、v、delay、alpha 是否已经初始化
    private boolean positionsInitialized;
    // 上一次是否处于 JScrollPane 模式
    private boolean previousScrollMode;
    // 当前逻辑上的活动歌词索引
    private int activeIndex = -1;

    // 每行之间的间距
    private int rowGap = ScaleUtil.scale(0);
    // 当前歌词目标在视口高度中的相对比例
    private double anchor = 0.5;
    // 歌词行的弹簧保留轻微回弹
    private double stiffness = 135;
    private double damping = 17.5;
    // 自动跟随视口接近临界阻尼，平稳停在高亮行的对齐位置
    private double autoScrollStiffness = 150;
    private double autoScrollDamping = 24.5;
    // 相邻行启动间隔 30 毫秒，跨多行时最多延迟 180 毫秒
    private double rowDelaySeconds = 0.030;
    private double maxCascadeDelay = 0.180;
    // activeIndex 发生变化时，是否允许播放动画
    private boolean animateActiveIndex = true;
    // activeIndex 变化后，视口是否自动跟随
    private boolean followActiveIndex = true;
    // 外部滚动时，是否添加行的弹性拖尾
    private boolean animateExternalScroll = true;
    // 外部滚动的拖尾位移强度，取值 0–1
    private double manualLagFactor = 0.35;

    // 视口状态，程序化滚动与外部滚动是不同的
    private JViewport watchedViewport;
    // 上一次记录的视口 Y 坐标
    private int lastViewY;
    // 上一次记录的视口高度
    private int lastExtentHeight = -1;
    // 本次视口移动是否由组件自身发起
    private boolean internalViewportChange;
    // 是否正在进行自动视口滚动
    private boolean autoScroll;
    // 是否还需要执行初次活动歌词定位
    private boolean pendingInitialActiveCenter = true;
    // 是否处于外部滚动所触发的动画过程
    private boolean externalScrolling;
    // 自动滚动动画计算的连续 Y 坐标
    private double scrollY;
    // 自动滚动的速度
    private double scrollV;
    // 自动滚动的最终目标 Y 坐标
    private double scrollGoal;
    // 监听视口位置及尺寸变化
    private final ChangeListener viewportListener = e -> viewportChanged();
    // 是否安装了普通容器模式的滚轮监听器
    private boolean directWheelInstalled;
    // 仅在不在 JScrollPane 内部时安装，否则它会阻止
    // AWT 将滚轮事件转发给 JScrollPane 祖先
    // 不在 JScrollPane 中时，用鼠标滚轮切换活动行
    private final MouseWheelListener directWheelListener = e -> {
        int d = (int) Math.signum(e.getPreciseWheelRotation());
        if (d != 0 && getModel().getSize() > 0) {
            setActiveIndex(Math.max(0, Math.min(getModel().getSize() - 1, activeIndex + d)));
            e.consume();
        }
    };

    private ListModel<E> watchedModel;
    private final ListDataListener dataListener = new ListDataListener() {
        public void intervalAdded(ListDataEvent e) {
            modelChanged();
        }

        public void intervalRemoved(ListDataEvent e) {
            modelChanged();
        }

        public void contentsChanged(ListDataEvent e) {
            modelChanged();
        }
    };
    private final PropertyChangeListener propertyListener = e -> {
        String p = e.getPropertyName();
        if ("model".equals(p)) {
            watchModel();
            modelChanged();
        } else if ("border".equals(p)
                || "cellRenderer".equals(p)
                || "font".equals(p)
                || "fixedCellHeight".equals(p)
                || "fixedCellWidth".equals(p)
                || "prototypeCellValue".equals(p)
                || "componentOrientation".equals(p)) {
            invalidateGeometry();
        }
    };

    public LyricList() {
        init();
    }

    public LyricList(ListModel<E> model) {
        init();
        setModel(model);
    }

    private void init() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(rendererPane);
        timer = new Timer(16, e -> {
            long now = System.nanoTime();
            double dt = Math.min(.038, (now - previousFrameNanos) * 1e-9);
            previousFrameNanos = now;
            advance(dt);
        });
        timer.setCoalesce(true);
        addPropertyChangeListener(propertyListener);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (timer != null && !timer.isRunning()) {
                    if (isScrollMode()) refreshViewportGeometry();
                    else snapToTargets();
                }
            }
        });
    }

    @Override
    public void setModel(ListModel<E> model) {
        super.setModel(model);
        watchModel();
        // activeIndex 的初始化和修正由 modelChanged() 处理；不主动修改 selectedIndex。
    }

    /**
     * 外部更新 activeIndex 主要方式：将 renderer 高亮行作为索引
     */
    public void updateActiveIndex() {
        ListCellRenderer<? super E> renderer = getCellRenderer();
        if (renderer instanceof LyricListRenderer) {
            LyricListRenderer lyricListRenderer = (LyricListRenderer) renderer;
            setActiveIndex(lyricListRenderer.getRow());
        }
    }

    /**
     * 线程安全的活动歌词索引更新入口，例如可从 JavaFX 音频回调中调用
     */
    public void setActiveIndex(int index) {
        setActiveIndex(index, true);
    }

    /**
     * 更新活动歌词索引
     *
     * @param index   新的活动歌词索引
     * @param animate 是否播放弹簧动画；为 false 时立即定位
     */
    public void setActiveIndex(int index, boolean animate) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setActiveIndex(index, animate));
            return;
        }
        int n = getModel().getSize();
        if (index < 0 || index >= n) return;
        int old = activeIndex;
        ensureGeometry();
        boolean playAnimation = animateActiveIndex && animate
                && isShowing() && positionsInitialized;
        if (playAnimation) cascade(index > old);
        activeIndex = index;
        if (!playAnimation) snapRows();
        if (isScrollMode() && followActiveIndex) {
            if (playAnimation) {
                startAutoScroll();
            } else {
                // 若视口尚未完成布局，稍后在视口尺寸变化时再定位
                pendingInitialActiveCenter = true;
                centerActiveIndexNow();
            }
        }
        if (playAnimation) startTimer();
        repaint();
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    // 注册 ListModel 数据变化监听器
    private void watchModel() {
        ListModel<E> m = getModel();
        if (m == watchedModel) return;
        if (watchedModel != null) watchedModel.removeListDataListener(dataListener);
        watchedModel = m;
        if (watchedModel != null) watchedModel.addListDataListener(dataListener);
    }

    // 数据变化后停止动画、清除旧布局缓存并重新验证组件
    private void modelChanged() {
        stopTimer();
        autoScroll = false;
        metricsDirty = true;
        positionsInitialized = false;

        int old = activeIndex;
        int n = getModel().getSize();
        int next = n == 0 || old < 0 ? -1 : Math.min(old, n - 1);
        activeIndex = next;

        // 模型变化本身不触发滚动；只有它导致 activeIndex 改变时才安排定位
        if (next != old) {
            pendingInitialActiveCenter = next >= 0;
            if (next >= 0 && isScrollMode()) {
                SwingUtilities.invokeLater(() -> {
                    if (pendingInitialActiveCenter && activeIndex >= 0 && isScrollMode())
                        centerActiveIndexNow();
                });
            } else {
                autoScroll = false;
                stopTimer();
            }
        }

        revalidate();
        repaint();
    }

    // 标记布局失效、取消自动滚动并请求重新布局
    private void invalidateGeometry() {
        metricsDirty = true;
        positionsInitialized = false;
        // 取消正在进行的自动滚动
        autoScroll = false;
        scrollV = 0;
        // 清除之前的弹簧动画状态
        externalScrolling = false;
        // 重新计算 Border Insets 和单元格坐标
        measuredWidth = Integer.MIN_VALUE;
        measuredViewportHeight = -1;
        stopTimer();
        revalidate();
        repaint();
    }

    private JViewport getViewport() {
        return getParent() instanceof JViewport ? (JViewport) getParent() : null;
    }

    // 判断当前是否位于 JViewport 中
    private boolean isScrollMode() {
        return getViewport() != null;
    }

    private int getViewportHeight() {
        JViewport vp = getViewport();
        if (vp == null) return getHeight();
        return Math.max(vp.getExtentSize().height, vp.getHeight());
    }

    // 计算扣除 Insets 和水平内边距后的 Renderer 可用宽度
    private int availableWidth() {
        Insets in = getInsets();
        JViewport vp = getViewport();
        int w = getWidth();
        if (w <= 0 && vp != null) w = vp.getExtentSize().width;
        if (w <= 0) w = 550;
        return Math.max(1, w - in.left - in.right);
    }

    // 它负责检查缓存是否有效、测量 Renderer 高度、重新计算累计坐标，以及初始化动画数组
    private void ensureGeometry() {
        int n = getModel().getSize();
        int w = availableWidth();
        int h = isScrollMode() ? getViewportHeight() : getHeight();
        boolean mode = isScrollMode();
        if (previousScrollMode != mode) {
            positionsInitialized = false;
            previousScrollMode = mode;
            pendingInitialActiveCenter = true;
        }
        if (heights.length != n || measuredWidth != w) metricsDirty = true;
        if (!metricsDirty && positionsInitialized && h == measuredViewportHeight) return;
        if (metricsDirty) {
            int[] measured = new int[n];
            double[] p = new double[n + 1];
            ListCellRenderer<? super E> r = getCellRenderer();
            for (int i = 0; i < n; i++) {
                E value = getModel().getElementAt(i);
                int ch = getFixedCellHeight();
                if (ch <= 0) {
                    if (r instanceof StableCellHeightProvider) {
                        ch = ((StableCellHeightProvider) r).getStableCellHeight(this, value, i, w);
                    } else {
                        // 普通 Renderer 使用原来的高度测量方式
                        Component c = r.getListCellRendererComponent(this, value, i, false, false);
                        c.setSize(w, Short.MAX_VALUE);
                        ch = c.getPreferredSize().height;
                    }
                }
                measured[i] = Math.max(1, ch);
                p[i + 1] = p[i] + measured[i] + rowGap;
            }
            heights = measured;
            prefix = p;
            measuredWidth = w;
            metricsDirty = false;
        }
        measuredViewportHeight = h;
        if (y.length != n) {
            y = new double[n];
            v = new double[n];
            delay = new double[n];
            positionsInitialized = false;
        }
        if (!positionsInitialized) {
            for (int i = 0; i < n; i++) {
                y[i] = targetY(i);
                v[i] = delay[i] = 0;
            }
            positionsInitialized = true;
        }
    }

    // 计算第 i 行在整个文档中的正常 Y 坐标
    private double documentY(int i) {
        return getInsets().top + prefix[i];
    }

    // 计算第 i 行的动画目标 Y 坐标
    private double targetY(int i) {
        if (heights.length == 0) return getInsets().top;
        if (isScrollMode() || activeIndex < 0) return documentY(i);
        int current = Math.max(0, Math.min(heights.length - 1, activeIndex));
        Insets in = getInsets();
        double space = Math.max(0, getHeight() - in.top - in.bottom);
        double activeTop = in.top + space * anchor - heights[current] * 0.5;
        return activeTop + prefix[i] - prefix[current];
    }

    // 根据各行高度、间距和上下留白计算整个文档高度
    private int documentHeight() {
        int n = heights.length;
        double content = n == 0 ? 0 : prefix[n] - rowGap;
        Insets in = getInsets();
        double result = in.top + content + in.bottom;
        return (int) Math.min(Integer.MAX_VALUE - 16L, Math.max(1, Math.ceil(result)));
    }

    /**
     * 关键点：在 JScrollPane 中使用具有真实内容高度的列表文档
     */
    @Override
    public Dimension getPreferredSize() {
        // JList 父类可能会在本类字段初始化之前调用此方法
        if (prefix == null || rendererPane == null || !isScrollMode()) return super.getPreferredSize();
        ensureGeometry();
        return new Dimension(Math.max(180, getWidth()), documentHeight());
    }

    // 列表宽度跟随视口，不需要水平滚动
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return isScrollMode();
    }

    // 表高度可以超过视口高度，因此能够进行真正的垂直滚动
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL ? 42 : 36;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL ? Math.max(45, (int) (r.height * 0.80))
                : Math.max(45, (int) (r.width * 0.80));
    }

    // 配置视口
    private void connectViewport() {
        JViewport vp = getViewport();
        if (vp == null && !directWheelInstalled) {
            addMouseWheelListener(directWheelListener);
            directWheelInstalled = true;
        } else if (vp != null && directWheelInstalled) {
            removeMouseWheelListener(directWheelListener);
            directWheelInstalled = false;
        }
        if (vp == watchedViewport) return;
        if (watchedViewport != null) watchedViewport.removeChangeListener(viewportListener);
        watchedViewport = vp;
        if (vp != null) {
            vp.addChangeListener(viewportListener);
            lastViewY = vp.getViewPosition().y;
            lastExtentHeight = getViewportHeight();
            pendingInitialActiveCenter = true;
            metricsDirty = true;
            positionsInitialized = false;
        }
    }

    // 检查视口尺寸是否变化，并在需要时执行初始定位
    private void refreshViewportGeometry() {
        JViewport vp = getViewport();
        if (vp == null) return;
        int h = getViewportHeight();
        if (h != measuredViewportHeight) {
            metricsDirty = true;
            positionsInitialized = false;
            revalidate();
            repaint();
        }
        if (pendingInitialActiveCenter && h > 0 && getWidth() > 0 && getModel().getSize() > 0)
            centerActiveIndexNow();
    }

    // 识别并适配外部滚动
    private void viewportChanged() {
        JViewport vp = getViewport();
        if (vp == null) return;
        int extent = getViewportHeight();
        int nextY = vp.getViewPosition().y;
        int dy = nextY - lastViewY;
        boolean resized = extent != lastExtentHeight;
        lastExtentHeight = extent;
        lastViewY = nextY;
        if (resized) {
            metricsDirty = true;
            positionsInitialized = false;
            revalidate();
            if (pendingInitialActiveCenter && extent > 0) {
                SwingUtilities.invokeLater(() -> {
                    if (pendingInitialActiveCenter) centerActiveIndexNow();
                });
            }
        }
        if (internalViewportChange || dy == 0) return;

        // 视口位置由用户或外部 API 控制，组件不得强行将其抢回
        autoScroll = false;
        scrollY = nextY;
        scrollV = 0;
        pendingInitialActiveCenter = false;
        if (!resized && animateExternalScroll && positionsInitialized && getModel().getSize() > 0) {
            boolean largeJump = Math.abs(dy) > Math.max(140, extent * .45);
            if (largeJump) snapRows(); // 拖动滚动条或整页跳转时直接归位，避免出现不可预测的位移
            else {
                if (!externalScrolling || Math.signum(dy) != Math.signum(externalScrollDirection)) {
                    cascade(dy > 0);
                }
                externalScrolling = true;
                externalScrollDirection = dy;
                injectLag(dy, false);
                startTimer();
            }
        }
    }

    // 记录最近一次外部滚动方向
    private int externalScrollDirection;

    // 设置视口 Y 坐标，同时标记为内部滚动并限制合法范围
    private void setViewportY(int value) {
        JViewport vp = getViewport();
        if (vp == null) return;
        int max = Math.max(0, documentHeight() - getViewportHeight());
        int next = Math.max(0, Math.min(max, value));
        int current = vp.getViewPosition().y;
        if (current == next) return;
        internalViewportChange = true;
        try {
            vp.setViewPosition(new Point(vp.getViewPosition().x, next));
        } finally {
            internalViewportChange = false;
            lastViewY = vp.getViewPosition().y;
        }
    }

    // 计算让当前活动行停在 anchor 位置时，视口应该滚动到哪里
    private int scrollTargetForActive() {
        JViewport vp = getViewport();
        if (vp == null || activeIndex < 0 || activeIndex >= heights.length) return 0;
        double centered = documentY(activeIndex) + heights[activeIndex] * 0.5 - getViewportHeight() * anchor;
        return Math.max(0, Math.min(Math.max(0, documentHeight() - getViewportHeight()),
                (int) Math.round(centered)));
    }

    // 立即将活动行定位到目标位置
    private void centerActiveIndexNow() {
        if (!isScrollMode() || activeIndex < 0 || getViewportHeight() <= 0) return;
        ensureGeometry();
        if (getHeight() <= 0) {
            revalidate();
            return;
        }
        autoScroll = false;
        scrollV = 0;
        scrollY = scrollGoal = scrollTargetForActive();
        setViewportY((int) Math.round(scrollY));
        pendingInitialActiveCenter = false;
    }

    // 设置自动滚动目标并启动动画
    private void startAutoScroll() {
        JViewport vp = getViewport();
        if (vp == null || getViewportHeight() <= 0) {
            pendingInitialActiveCenter = true;
            return;
        }
        scrollY = vp.getViewPosition().y;
        scrollGoal = scrollTargetForActive();
        autoScroll = Math.abs(scrollGoal - scrollY) > .5;
        externalScrolling = false;
        pendingInitialActiveCenter = false;
        if (autoScroll) startTimer();
    }

    // 第一条可见歌词立即移动，后续歌词依次延迟跟随并弹性回归
    private void cascade(boolean forward) {
        if (y.length == 0) return;
        Rectangle visible = isScrollMode()
                ? new Rectangle(0, lastViewY, getWidth(), getViewportHeight())
                : new Rectangle(0, 0, getWidth(), getHeight());
        int first = -1, last = -1;
        for (int i = 0; i < y.length; i++) {
            if (y[i] + heights[i] > visible.y && y[i] < visible.y + visible.height) {
                if (first < 0) first = i;
                last = i;
            }
        }
        if (first < 0) {
            first = Math.max(0, activeIndex - 3);
            last = Math.min(y.length - 1, activeIndex + 3);
        }
        for (int i = 0; i < y.length; i++) {
            int rank = Math.max(0, forward ? i - first : last - i);
            delay[i] = Math.min(maxCascadeDelay, rank * rowDelaySeconds);
        }
    }

    // 按滚动距离为各行加入拖尾位移
    private void injectLag(int delta, boolean programmatic) {
        if (delta == 0) return;
        int first = -1, last = -1;
        double top = lastViewY - 120, bottom = lastViewY + getViewportHeight() + 120;
        for (int i = 0; i < y.length; i++) {
            if (targetY(i) + heights[i] > top && targetY(i) < bottom) {
                if (first < 0) first = i;
                last = i;
            }
        }
        if (first < 0) return;
        boolean forward = delta > 0;
        double limit = Math.max(45, Math.min(125, getViewportHeight() * .30));
        for (int i = Math.max(0, first - 2); i <= Math.min(y.length - 1, last + 2); i++) {
            int rank = Math.max(0, forward ? i - first : last - i);
            double influence = Math.min(1., .23 + rank * .18);
            double scale = programmatic ? 1 : manualLagFactor;
            double target = targetY(i);
            y[i] = target + Math.max(-limit, Math.min(limit,
                    (y[i] - target) + delta * influence * scale));
        }
    }

    // 立即清除每行的速度和延迟，恢复行的目标位置和目标透明度
    private void snapRows() {
        ensureGeometry();
        Arrays.fill(v, 0);
        Arrays.fill(delay, 0);
        for (int i = 0; i < y.length; i++) y[i] = targetY(i);
        externalScrolling = false;
        repaint();
    }

    // 立即结束所有行的弹簧动画，不改变视口位置
    public void snapToTargets() {
        snapRows();
        autoScroll = false;
        stopTimer();
        repaint();
    }

    private void startTimer() {
        if (timer == null || !isShowing()) return;
        if (!timer.isRunning()) {
            previousFrameNanos = System.nanoTime();
            timer.start();
        }
    }

    private void stopTimer() {
        if (timer != null) timer.stop();
    }

    // 执行动画参数计算
    private void advance(double dt) {
        if (dt <= 0) return;
        ensureGeometry();
        int oldViewportY = isScrollMode() ? getViewport().getViewPosition().y : 0;
        int steps = Math.max(1, (int) Math.ceil(dt / MAX_STEP));
        double sub = dt / steps;
        for (int s = 0; s < steps; s++) {
            if (autoScroll && isScrollMode()) {
                // 视口滚动采用接近临界阻尼的运动，行弹簧则保留更柔和的回弹。
                double a = autoScrollStiffness * (scrollGoal - scrollY) - autoScrollDamping * scrollV;
                scrollV += a * sub;
                scrollY += scrollV * sub;
                if (Math.abs(scrollGoal - scrollY) < .35 && Math.abs(scrollV) < 2) {
                    scrollY = scrollGoal;
                    scrollV = 0;
                    autoScroll = false;
                }
            }
        }
        if (isScrollMode() && (autoScroll || scrollY != oldViewportY)) {
            setViewportY((int) Math.round(scrollY));
            int newViewportY = getViewport().getViewPosition().y;
            injectLag(newViewportY - oldViewportY, true);
            // 到达滚动边界时，将浮点位置校正为视口的实际坐标
            if (newViewportY == oldViewportY && Math.abs(scrollY - oldViewportY) > 2)
                scrollY = oldViewportY;
        }
        for (int s = 0; s < steps; s++) {
            for (int i = 0; i < y.length; i++) {
                if (delay[i] > 0) {
                    delay[i] = Math.max(0, delay[i] - sub);
                } else {
                    double target = targetY(i);
                    double a = stiffness * (target - y[i]) - damping * v[i];
                    v[i] += a * sub;
                    y[i] += v[i] * sub;
                    if (Math.abs(target - y[i]) < EPS_Y && Math.abs(v[i]) < EPS_V) {
                        y[i] = target;
                        v[i] = 0;
                    }
                }
            }
        }
        boolean moving = autoScroll;
        if (!moving) for (int i = 0; i < y.length; i++) {
            if (delay[i] > 0 || v[i] != 0 || Math.abs(targetY(i) - y[i]) > EPS_Y) {
                moving = true;
                break;
            }
        }
        repaint();
        if (!moving) {
            externalScrolling = false;
            stopTimer();
        }
    }

    // 组件加入显示层次时连接视口、准备布局和初始定位
    @Override
    public void addNotify() {
        super.addNotify();
        connectViewport();
        ensureGeometry();
        if (isScrollMode() && pendingInitialActiveCenter) {
            SwingUtilities.invokeLater(() -> {
                if (pendingInitialActiveCenter) centerActiveIndexNow();
            });
        }
    }

    // 组件退出显示层次时停止定时器、移除视口和直接滚轮监听器
    @Override
    public void removeNotify() {
        stopTimer();
        if (watchedViewport != null) watchedViewport.removeChangeListener(viewportListener);
        watchedViewport = null;
        if (directWheelInstalled) {
            removeMouseWheelListener(directWheelListener);
            directWheelInstalled = false;
        }
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics.create();
        try {
            ensureGeometry();
            Insets in = getInsets();
            Rectangle clip = g2d.getClipBounds();
            int top = clip == null ? 0 : clip.y;
            int bottom = clip == null ? getHeight() : clip.y + clip.height;
            int w = availableWidth(), x = in.left;
            ListCellRenderer<? super E> r = getCellRenderer();
            for (int i = 0; i < y.length; i++) {
                int yy = (int) Math.round(y[i]);
                if (yy + heights[i] < top || yy > bottom) continue;
                Component c = r.getListCellRendererComponent(this,
                        getModel().getElementAt(i), i,
                        getSelectionModel().isSelectedIndex(i),
                        hasFocus() && getLeadSelectionIndex() == i);
                Composite old = g2d.getComposite();
                c.setSize(w, heights[i]);
                if (c instanceof Container) layoutRenderer((Container) c);
                rendererPane.paintComponent(g2d, c, this, x, yy, w, heights[i], true);
                g2d.setComposite(old);
            }
        } finally {
            g2d.dispose();
        }
    }

    private static void layoutRenderer(Container c) {
        c.doLayout();
        for (Component child : c.getComponents())
            if (child instanceof Container) layoutRenderer((Container) child);
    }

    // 命中测试依据动画后的实际绘制位置，而不是未应用动画的单元格边界
    @Override
    public int locationToIndex(Point p) {
        if (p == null || getModel().getSize() == 0 || prefix == null) return -1;
        ensureGeometry();
        int best = -1;
        double dist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < y.length; i++) {
            double d = Math.abs(p.y - (y[i] + heights[i] * .5));
            if (d < dist) {
                dist = d;
                best = i;
            }
        }
        return best;
    }

    @Override
    public Point indexToLocation(int index) {
        if (index < 0 || index >= getModel().getSize() || prefix == null) return null;
        ensureGeometry();
        return new Point(getInsets().left, (int) Math.round(y[index]));
    }

    @Override
    public Rectangle getCellBounds(int first, int second) {
        if (first < 0 || second < 0 || first >= getModel().getSize()
                || second >= getModel().getSize() || prefix == null) return null;
        ensureGeometry();
        double top = Double.POSITIVE_INFINITY, bottom = Double.NEGATIVE_INFINITY;
        for (int i = Math.min(first, second); i <= Math.max(first, second); i++) {
            top = Math.min(top, y[i]);
            bottom = Math.max(bottom, y[i] + heights[i]);
        }
        int t = (int) Math.floor(top), b = (int) Math.ceil(bottom);
        return new Rectangle(getInsets().left, t, availableWidth(), Math.max(1, b - t));
    }

    /**
     * 禁止 JList 的默认键盘选择行为通过 ensureIndexIsVisible() 隐式滚动视口
     * 视口只由 activeIndex 更新或外部视口操作控制
     * 如需外部滚动，请直接操作 JScrollPane 的滚动条或其 JViewport
     */
    @Override
    public void ensureIndexIsVisible(int index) {
        // 有意留空：selectedIndex 的变化不得引起视口滚动
    }
}
