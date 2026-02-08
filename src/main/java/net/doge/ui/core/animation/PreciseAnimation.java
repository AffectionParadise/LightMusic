//package net.doge.ui.core.animation;
//
//import net.doge.ui.core.animation.handler.AnimationUpdatedHandler;
//
//import javax.swing.*;
//
//public class PreciseAnimation {
//    private Timer timer;
//    private long startTime;
//    private int duration;
//    private AnimationUpdatedHandler handler;
//    private float progress;
//
//    public PreciseAnimation(int duration, AnimationUpdatedHandler handler) {
//        this.duration = duration;
//        this.handler = handler;
//
//        // 使用高精度 Timer，间隔尽可能短
//        timer = new Timer(1, e -> {
//            // 计算已经过去的时间
//            long elapsed = System.currentTimeMillis() - startTime;
//            // 计算进度
//            progress = Math.min(1f, (float) elapsed / this.duration);
//            // 更新动画状态
//            if (this.handler != null) this.handler.handle(progress);
//            // 检查是否达到总时长
//            if (elapsed >= duration) {
//                timer.stop();
//                // 确保最终状态准确
//                if (this.handler != null) this.handler.handle(1f);
//                animationCompleted();
//            }
//        });
//    }
//
//    public void start() {
//        // 记录动画开始时间
//        startTime = System.currentTimeMillis();
//        timer.start();
//    }
//
//    public boolean isRunning() {
//        return timer.isRunning();
//    }
//
//    protected void animationCompleted() {
//    }
//}
