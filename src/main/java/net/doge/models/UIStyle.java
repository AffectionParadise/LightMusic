package net.doge.models;

import lombok.Data;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.ImageConstants;
import net.doge.constants.UIStyleConstants;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/12
 */
@Data
public class UIStyle {
    // 风格类型
    private int styleType;
    // 风格名称
    private String styleName;
    // 对应的图片路径
    private String styleImgPath;
    // 图片
    private BufferedImage img;
    // 小图
    private BufferedImage imgThumb;
    // 背景颜色
    private Color bgColor;
    // 播放列表前景颜色
    private Color foreColor;
    // 播放列表选中颜色
    private Color selectedColor;
    // 歌词文字颜色
    private Color lrcColor;
    // 歌词高亮颜色
    private Color highlightColor;
    // 右上角标签颜色
    private Color labelColor;
    // 进度条边框和填充颜色
    private Color timeBarColor;
    // 按钮颜色
    private Color buttonColor;
    // 滚动条颜色
    private Color scrollBarColor;
    // 滑动条颜色
    private Color sliderColor;
    // 频谱颜色
    private Color spectrumColor;
    // 菜单项颜色
    private Color menuItemColor;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public boolean isPreDefined() {
        return styleType == UIStyleConstants.PRE;
    }

    public boolean isCustom() {
        return styleType == UIStyleConstants.CUSTOM;
    }

    public boolean isPureColor() {
        return bgColor != null;
    }

    private void callback() {
        if (invokeLater != null) {
            invokeLater.run();
            // 调用后丢弃
//            invokeLater = null;
        }
    }

    public void setStyleImgPath(String styleImgPath) {
        this.styleImgPath = styleImgPath;
        if (StringUtils.isEmpty(styleImgPath)) return;
        GlobalExecutors.imageExecutor.execute(() -> {
            img = ImageUtils.read(styleImgPath);
            imgThumb = ImageUtils.setRadius(ImageUtils.width(img, ImageConstants.mvCoverWidth), 10);
            // 控制高度不超过阈值
            if (imgThumb != null && imgThumb.getHeight() > ImageConstants.mvCoverMaxHeight)
                imgThumb = ImageUtils.height(imgThumb, ImageConstants.mvCoverMaxHeight);
            callback();
        });
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        if (bgColor == null) return;
        GlobalExecutors.imageExecutor.execute(() -> {
            img = ImageUtils.dyeRect(2, 1, bgColor);
            imgThumb = ImageUtils.setRadius(ImageUtils.width(img, ImageConstants.mvCoverWidth), 10);
            callback();
        });
    }

    public UIStyle(Integer styleType, String styleName, String styleImgPath,
                   Color foreColor, Color selectedColor, Color lrcColor, Color highlightColor, Color labelColor, Color timeBarColor, Color buttonColor, Color scrollBarColor, Color sliderColor, Color spectrumColor, Color menuItemColor) {
        this.styleType = styleType;
        this.styleName = styleName;
        setStyleImgPath(styleImgPath);
        this.foreColor = foreColor;
        this.selectedColor = selectedColor;
        this.lrcColor = lrcColor;
        this.highlightColor = highlightColor;
        this.labelColor = labelColor;
        this.timeBarColor = timeBarColor;
        this.buttonColor = buttonColor;
        this.scrollBarColor = scrollBarColor;
        this.sliderColor = sliderColor;
        this.spectrumColor = spectrumColor;
        this.menuItemColor = menuItemColor;
    }

    public UIStyle(Integer styleType, String styleName, String styleImgPath, Color bgColor,
                   Color foreColor, Color selectedColor, Color lrcColor, Color highlightColor, Color labelColor, Color timeBarColor, Color buttonColor, Color scrollBarColor, Color sliderColor, Color spectrumColor, Color menuItemColor) {
        this.styleType = styleType;
        this.styleName = styleName;
        setStyleImgPath(styleImgPath);
        setBgColor(bgColor);
        this.foreColor = foreColor;
        this.selectedColor = selectedColor;
        this.lrcColor = lrcColor;
        this.highlightColor = highlightColor;
        this.labelColor = labelColor;
        this.timeBarColor = timeBarColor;
        this.buttonColor = buttonColor;
        this.scrollBarColor = scrollBarColor;
        this.sliderColor = sliderColor;
        this.spectrumColor = spectrumColor;
        this.menuItemColor = menuItemColor;
    }
}
