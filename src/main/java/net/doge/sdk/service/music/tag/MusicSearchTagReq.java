package net.doge.sdk.service.music.tag;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.service.music.tag.impl.musicsearchtag.MeMusicSearchTagReq;

public class MusicSearchTagReq {
    private static MusicSearchTagReq instance;

    private MusicSearchTagReq() {
    }

    public static MusicSearchTagReq getInstance() {
        if (instance == null) instance = new MusicSearchTagReq();
        return instance;
    }

    /**
     * 加载节目搜索子标签
     *
     * @return
     */
    public void initProgramSearchTag() {
        // 猫耳
        Tags.programSearchTags.put(I18n.getText("defaultTag"), new String[]{" "});

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(() -> MeMusicSearchTagReq.getInstance().initProgramSearchTag());
        executor.await();
    }
}
