package net.doge.sdk.service.music.tag;

import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.service.music.tag.impl.hotsongtag.KgHotSongTagReq;
import net.doge.sdk.service.music.tag.impl.hotsongtag.NcHotSongTagReq;

public class HotSongTagReq {
    private static HotSongTagReq instance;

    private HotSongTagReq() {
    }

    public static HotSongTagReq getInstance() {
        if (instance == null) instance = new HotSongTagReq();
        return instance;
    }

    /**
     * 加载飙升歌曲标签
     *
     * @return
     */
    public void initHotSongTag() {
        // 网易云 酷狗 酷狗 酷狗 酷狗 音乐磁场 咕咕咕音乐
        Tags.hotSongTags.put("默认", new String[]{"", "1", "", "", "", "index", "index"});

        // 酷狗
        Tags.hotSongTags.put("精选好歌随心听", new String[]{"", "1", "", "", "", "", ""});
        Tags.hotSongTags.put("经典怀旧金曲", new String[]{"", "2", "", "", "", "", ""});
        Tags.hotSongTags.put("热门好歌精选", new String[]{"", "3", "", "", "", "", ""});
        Tags.hotSongTags.put("小众宝藏佳作", new String[]{"", "4", "", "", "", "", ""});
        Tags.hotSongTags.put("未知", new String[]{"", "5", "", "", "", "", ""});
        Tags.hotSongTags.put("Vip 专属推荐", new String[]{"", "6", "", "", "", "", ""});

        // 音乐磁场
        Tags.hotSongTags.put("热门", new String[]{"", "", "", "", "", "index-0-2", "index-0-hot"});
        Tags.hotSongTags.put("月榜", new String[]{"", "", "", "", "", "index-0-3", ""});
        Tags.hotSongTags.put("周榜", new String[]{"", "", "", "", "", "index-0-4", ""});
        Tags.hotSongTags.put("日榜", new String[]{"", "", "", "", "", "index-0-5", ""});
        Tags.hotSongTags.put("华语", new String[]{"", "", "", "", "", "forum-1", "forum-1"});
        Tags.hotSongTags.put("日韩", new String[]{"", "", "", "", "", "forum-2", "forum-7"});
        Tags.hotSongTags.put("欧美", new String[]{"", "", "", "", "", "forum-3", "forum-3"});
        Tags.hotSongTags.put("Remix", new String[]{"", "", "", "", "", "forum-4", ""});
        Tags.hotSongTags.put("纯音乐", new String[]{"", "", "", "", "", "forum-5", "forum-6"});
//        Tags.hotSongTag.put("异次元", new String[]{"", "", "", "", "", "forum-13", ""});
        Tags.hotSongTags.put("音友", new String[]{"", "", "", "", "", "forum-6", ""});
        Tags.hotSongTags.put("互助", new String[]{"", "", "", "", "", "forum-7", ""});
        Tags.hotSongTags.put("站务", new String[]{"", "", "", "", "", "forum-9", ""});

        // 咕咕咕音乐
        Tags.hotSongTags.put("音乐分享区", new String[]{"", "", "", "", "", "", "forum-12"});
        Tags.hotSongTags.put("伤感", new String[]{"", "", "", "", "", "", "forum-8"});
        Tags.hotSongTags.put("粤语", new String[]{"", "", "", "", "", "", "forum-2"});
        Tags.hotSongTags.put("青春", new String[]{"", "", "", "", "", "", "forum-5"});
        Tags.hotSongTags.put("分享", new String[]{"", "", "", "", "", "", "forum-11"});
        Tags.hotSongTags.put("温柔男友音", new String[]{"", "", "", "", "", "", "forum-10"});
        Tags.hotSongTags.put("DJ", new String[]{"", "", "", "", "", "", "forum-9"});

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(() -> NcHotSongTagReq.getInstance().initHotSongTag());
        executor.submit(() -> KgHotSongTagReq.getInstance().initThemeSongTag());
        executor.submit(() -> KgHotSongTagReq.getInstance().initFmTag());
        executor.submit(() -> KgHotSongTagReq.getInstance().initIpTag());
        executor.await();
    }
}
