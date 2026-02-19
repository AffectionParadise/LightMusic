package net.doge.sdk.service.playlist.tag;

import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.service.playlist.tag.impl.recplaylisttag.*;

public class RecPlaylistTagReq {
    private static RecPlaylistTagReq instance;

    private RecPlaylistTagReq() {
    }

    public static RecPlaylistTagReq getInstance() {
        if (instance == null) instance = new RecPlaylistTagReq();
        return instance;
    }

    /**
     * 加载推荐歌单标签
     *
     * @return
     */
    public void initRecPlaylistTag() {
        // 网易云 酷狗 QQ 猫耳 5sing
        Tags.recPlaylistTags.put("默认", new String[]{"", " ", "10000000", " ", " "});

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(() -> NcRecPlaylistTagReq.getInstance().initRecPlaylistTag());
        executor.submit(() -> KgRecPlaylistTagReq.getInstance().initRecPlaylistTag());
        executor.submit(() -> QqRecPlaylistTagReq.getInstance().initRecPlaylistTag());
        executor.submit(() -> MeRecPlaylistTagReq.getInstance().initRecPlaylistTag());
        executor.submit(() -> FsRecPlaylistTagReq.getInstance().initRecPlaylistTag());
        executor.await();
    }
}
