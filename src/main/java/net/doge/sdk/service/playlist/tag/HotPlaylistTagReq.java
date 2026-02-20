package net.doge.sdk.service.playlist.tag;

import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.service.playlist.tag.impl.hotplaylisttag.*;

public class HotPlaylistTagReq {
    private static HotPlaylistTagReq instance;

    private HotPlaylistTagReq() {
    }

    public static HotPlaylistTagReq getInstance() {
        if (instance == null) instance = new HotPlaylistTagReq();
        return instance;
    }

    /**
     * 加载歌单标签
     *
     * @return
     */
    public void initHotPlaylistTag() {
        // 网易云精品歌单 网易云网友精选碟 酷狗 酷狗 QQ 酷我 咪咕 千千 猫耳 猫耳探索 5sing
        Tags.hotPlaylistTags.put("默认", new String[]{"全部", "全部", " ", "", "10000000", "", "", " ", " ", "", " "});

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(() -> NcHotPlaylistTagReq.getInstance().initHighQualityPlaylistTag());
        executor.submit(() -> NcHotPlaylistTagReq.getInstance().initPickedPlaylistTag());
        executor.submit(() -> KgHotPlaylistTagReq.getInstance().initHotPlaylistTag());
        executor.submit(() -> KgHotPlaylistTagReq.getInstance().initIpTag());
        executor.submit(() -> QqHotPlaylistTagReq.getInstance().initHotPlaylistTag());
        executor.submit(() -> KwHotPlaylistTagReq.getInstance().initHotPlaylistTag());
        executor.submit(() -> MgHotPlaylistTagReq.getInstance().initHotPlaylistTag());
        executor.submit(() -> QiHotPlaylistTagReq.getInstance().initHotPlaylistTag());
        executor.submit(() -> MeHotPlaylistTagReq.getInstance().initHotPlaylistTag());
        executor.submit(() -> MeHotPlaylistTagReq.getInstance().initExpPlaylistTag());
        executor.submit(() -> FsHotPlaylistTagReq.getInstance().initHotPlaylistTag());
        executor.await();
    }
}
