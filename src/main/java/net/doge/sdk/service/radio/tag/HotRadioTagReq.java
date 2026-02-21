package net.doge.sdk.service.radio.tag;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.service.radio.tag.impl.*;

public class HotRadioTagReq {
    private static HotRadioTagReq instance;

    private HotRadioTagReq() {
    }

    public static HotRadioTagReq getInstance() {
        if (instance == null) instance = new HotRadioTagReq();
        return instance;
    }

    /**
     * 加载电台标签
     *
     * @return
     */
    public void initRadioTag() {
        // 网易云 网易云 喜马拉雅 喜马拉雅 喜马拉雅 猫耳 豆瓣 豆瓣 咪咕
        Tags.radioTags.put(I18n.getText("defaultTag"), new String[]{"", "", "", "", "", "0 0 0", "", " ", ""});

        // 喜马拉雅频道
        Tags.radioTags.put("小说", new String[]{"", "", "", "", "7", "", "", "", ""});
        Tags.radioTags.put("儿童", new String[]{"", "", "", "", "11", "", "", "", ""});
        Tags.radioTags.put("相声小品", new String[]{"", "", "", "", "9", "", "", "", ""});
        Tags.radioTags.put("评书", new String[]{"", "", "", "", "10", "", "", "", ""});
        Tags.radioTags.put("娱乐", new String[]{"", "", "", "", "13", "", "", "", ""});
        Tags.radioTags.put("悬疑", new String[]{"", "", "", "", "14", "", "", "", ""});
        Tags.radioTags.put("人文", new String[]{"", "", "", "", "17", "", "", "", ""});
        Tags.radioTags.put("国学", new String[]{"", "", "", "", "18", "", "", "", ""});
        Tags.radioTags.put("头条", new String[]{"", "", "", "", "24", "", "", "", ""});
        Tags.radioTags.put("音乐", new String[]{"", "", "", "", "19", "", "", "", ""});
        Tags.radioTags.put("历史", new String[]{"", "", "", "", "16", "", "", "", ""});
        Tags.radioTags.put("情感", new String[]{"", "", "", "", "20", "", "", "", ""});
        Tags.radioTags.put("投资理财", new String[]{"", "", "", "", "26", "", "", "", ""});
        Tags.radioTags.put("个人提升", new String[]{"", "", "", "", "31", "", "", "", ""});
        Tags.radioTags.put("健康", new String[]{"", "", "", "", "22", "", "", "", ""});
        Tags.radioTags.put("生活", new String[]{"", "", "", "", "21", "", "", "", ""});
        Tags.radioTags.put("影视", new String[]{"", "", "", "", "15", "", "", "", ""});
        Tags.radioTags.put("商业管理", new String[]{"", "", "", "", "27", "", "", "", ""});
        Tags.radioTags.put("英语", new String[]{"", "", "", "", "29", "", "", "", ""});
        Tags.radioTags.put("少儿素养", new String[]{"", "", "", "", "12", "", "", "", ""});
        Tags.radioTags.put("科技", new String[]{"", "", "", "", "28", "", "", "", ""});
        Tags.radioTags.put("教育考试", new String[]{"", "", "", "", "32", "", "", "", ""});
        Tags.radioTags.put("体育", new String[]{"", "", "", "", "25", "", "", "", ""});
        Tags.radioTags.put("小语种", new String[]{"", "", "", "", "30", "", "", "", ""});
        Tags.radioTags.put("广播剧", new String[]{"", "", "", "", "8", "", "", "", ""});
        Tags.radioTags.put("汽车", new String[]{"", "", "", "", "23", "", "", "", ""});

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(() -> NcHotRadioTagReq.getInstance().initHotRadioTag());
        executor.submit(() -> NcHotRadioTagReq.getInstance().initRecRadioTag());
        executor.submit(() -> MgHotRadioTagReq.getInstance().initHotRadioTag());
        executor.submit(() -> XmHotRadioTagReq.getInstance().initRadioTag());
        executor.submit(() -> XmHotRadioTagReq.getInstance().initRankTag());
        executor.submit(() -> MeHotRadioTagReq.getInstance().initRadioTag());
        executor.submit(() -> DbHotRadioTagReq.getInstance().initRadioTag());
        executor.submit(() -> DbHotRadioTagReq.getInstance().initGameRadioTag());
        executor.await();
    }
}
