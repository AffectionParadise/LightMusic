package net.doge.sdk.service.music.tag;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.service.music.tag.impl.newsongtag.FsNewSongTagReq;
import net.doge.sdk.service.music.tag.impl.newsongtag.KgNewSongTagReq;
import net.doge.sdk.service.music.tag.impl.newsongtag.NcNewSongTagReq;

public class NewSongTagReq {
    private static NewSongTagReq instance;

    private NewSongTagReq() {
    }

    public static NewSongTagReq getInstance() {
        if (instance == null) instance = new NewSongTagReq();
        return instance;
    }

    /**
     * 加载新歌标签
     *
     * @return
     */
    public void initNewSongTag() {
        // 网易云 网易云 酷狗 酷狗 QQ 音乐磁场 咕咕咕音乐 5sing
        Tags.newSongTags.put(I18n.getText("defaultTag"), new String[]{"0", "", "1", "", "5", "", "", " "});

        Tags.newSongTags.put("华语", new String[]{"7", "", "1", "", "5", "forum-1", "forum-1", ""});
        Tags.newSongTags.put("内地", new String[]{"", "", "", "", "1", "", "", ""});
        Tags.newSongTags.put("港台", new String[]{"", "", "", "", "6", "", "", ""});
        Tags.newSongTags.put("欧美", new String[]{"96", "", "2", "", "2", "forum-2", "forum-3", ""});
        Tags.newSongTags.put("韩国", new String[]{"16", "", "4", "", "4", "", "", ""});
        Tags.newSongTags.put("日本", new String[]{"8", "", "5", "", "3", "", "", ""});
        Tags.newSongTags.put("日韩", new String[]{"", "", "3", "", "", "forum-3", "forum-7", ""});

        // 音乐磁场
        Tags.newSongTags.put("Remix", new String[]{"", "", "", "", "", "forum-4", "", ""});
        Tags.newSongTags.put("纯音乐", new String[]{"", "", "", "", "", "forum-5", "", ""});
//        Tags.newSongTag.put("异次元", new String[]{"", "", "", "", "", "forum-13", "", ""});
        Tags.newSongTags.put("音友", new String[]{"", "", "", "", "", "forum-6", "", ""});
        Tags.newSongTags.put("互助", new String[]{"", "", "", "", "", "forum-7", "", ""});
        Tags.newSongTags.put("站务", new String[]{"", "", "", "", "", "forum-9", "", ""});

        // 咕咕咕音乐
        Tags.newSongTags.put("音乐分享区", new String[]{"", "", "", "", "", "", "forum-12", ""});
        Tags.newSongTags.put("伤感", new String[]{"", "", "", "", "", "", "forum-8", ""});
        Tags.newSongTags.put("粤语", new String[]{"", "", "", "", "", "", "forum-2", ""});
        Tags.newSongTags.put("青春", new String[]{"", "", "", "", "", "", "forum-5", ""});
        Tags.newSongTags.put("分享", new String[]{"", "", "", "", "", "", "forum-11", ""});
        Tags.newSongTags.put("温柔男友音", new String[]{"", "", "", "", "", "", "forum-10", ""});
        Tags.newSongTags.put("DJ", new String[]{"", "", "", "", "", "", "forum-9", ""});

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(() -> NcNewSongTagReq.getInstance().initNewSongTag());
        executor.submit(() -> KgNewSongTagReq.getInstance().initNewSongTag());
        executor.submit(() -> FsNewSongTagReq.getInstance().initNewSongTag());
        executor.await();
    }
}
