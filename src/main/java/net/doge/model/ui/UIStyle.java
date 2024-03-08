package net.doge.model.ui;

import lombok.Data;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.UIStyleConstants;
import net.doge.constant.ui.ImageConstants;
import net.doge.util.common.StringUtil;
import net.doge.util.lmdata.LMStyleManager;
import net.doge.util.ui.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description 主题
 * @Date 2020/12/12
 */
@Data
public class UIStyle {
    // 类型
    private int type;
    // 名称
    private String name;
    // 对应的图片 key
    private String imgKey;
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
    // 标签文字颜色
    private Color textColor;
    // 进度条边框和填充颜色
    private Color timeBarColor;
    // 图标颜色
    private Color iconColor;
    // 滚动条颜色
    private Color scrollBarColor;
    // 滑动条颜色
    private Color sliderColor;
    // 频谱颜色
    private Color spectrumColor;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public boolean isPreDefined() {
        return type == UIStyleConstants.PRE;
    }

    public boolean isCustom() {
        return type == UIStyleConstants.CUSTOM;
    }

    public boolean hasImg() {
        return img != null;
    }

    private void callback() {
        if (invokeLater == null) return;
        invokeLater.run();
        // 调用后丢弃
        invokeLater = null;
    }

    public void setImgKey(String imgKey) {
        this.imgKey = imgKey;
        if (StringUtil.isEmpty(imgKey)) {
            img = imgThumb = null;
            return;
        }
        GlobalExecutors.requestExecutor.execute(() -> {
            img = isPreDefined() ? LMStyleManager.getImage(imgKey) : ImageUtil.read(imgKey);
            if (img == null) return;
            imgThumb = ImageUtil.radius(ImageUtil.width(img, ImageConstants.MV_COVER_WIDTH), 10);
            // 控制高度不超过阈值
            if (imgThumb != null && imgThumb.getHeight() > ImageConstants.MV_COVER_MAX_HEIGHT)
                imgThumb = ImageUtil.height(imgThumb, ImageConstants.MV_COVER_MAX_HEIGHT);
            callback();
        });
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        if (bgColor == null) return;
        GlobalExecutors.requestExecutor.execute(() -> {
            img = ImageUtil.dyeRect(2, 1, bgColor);
            imgThumb = ImageUtil.radius(ImageUtil.width(img, ImageConstants.MV_COVER_WIDTH), 10);
            callback();
        });
    }

    public UIStyle(Integer type, String name, String imgKey,
                   Color foreColor, Color selectedColor, Color lrcColor, Color highlightColor,
                   Color textColor, Color timeBarColor, Color iconColor, Color scrollBarColor, Color sliderColor, Color spectrumColor) {
        this.type = type;
        this.name = name;
        setImgKey(imgKey);
        this.foreColor = foreColor;
        this.selectedColor = selectedColor;
        this.lrcColor = lrcColor;
        this.highlightColor = highlightColor;
        this.textColor = textColor;
        this.timeBarColor = timeBarColor;
        this.iconColor = iconColor;
        this.scrollBarColor = scrollBarColor;
        this.sliderColor = sliderColor;
        this.spectrumColor = spectrumColor;
    }

    public UIStyle(Integer type, String name, String imgKey, Color bgColor,
                   Color foreColor, Color selectedColor, Color lrcColor, Color highlightColor,
                   Color textColor, Color timeBarColor, Color iconColor, Color scrollBarColor, Color sliderColor, Color spectrumColor) {
        this.type = type;
        this.name = name;
        setImgKey(imgKey);
        setBgColor(bgColor);
        this.foreColor = foreColor;
        this.selectedColor = selectedColor;
        this.lrcColor = lrcColor;
        this.highlightColor = highlightColor;
        this.textColor = textColor;
        this.timeBarColor = timeBarColor;
        this.iconColor = iconColor;
        this.scrollBarColor = scrollBarColor;
        this.sliderColor = sliderColor;
        this.spectrumColor = spectrumColor;
    }
}
