package net.doge.sdk.service.artist.tag;

import net.doge.constant.service.tag.Tags;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.service.artist.tag.impl.KgArtistTagReq;
import net.doge.sdk.service.artist.tag.impl.NcArtistTagReq;
import net.doge.sdk.service.artist.tag.impl.QqArtistTagReq;

public class ArtistTagReq {
    private static ArtistTagReq instance;

    private ArtistTagReq() {
    }

    public static ArtistTagReq getInstance() {
        if (instance == null) instance = new ArtistTagReq();
        return instance;
    }

    /**
     * 加载歌手标签
     *
     * @return
     */
    public void initArtistTag() {
        // 网易云 网易云 网易云 酷狗 酷狗 QQ 酷我 酷我 咪咕 千千 猫耳
        Tags.artistTags.put("默认", new String[]{"1", "", "", "0 0", "", "-100 -100 -100 -100", "11", "0 ", "huayu-nan", "  ", " "});

        Tags.artistTags.put("男", new String[]{"", "1 -1 -1", "", "1 0", "", "0 -100 -100 -100", "", "", "", "  男", ""});
        Tags.artistTags.put("女", new String[]{"", "2 -1 -1", "", "2 0", "", "1 -100 -100 -100", "", "", "", "  女", ""});
        Tags.artistTags.put("组合", new String[]{"", "3 -1 -1", "", "3 0", "", "2 -100 -100 -100", "16", "", "", "  组合", ""});
        Tags.artistTags.put("乐队", new String[]{"", "", "", "", "", "", "", "", "", "  乐队", ""});
        Tags.artistTags.put("华语", new String[]{"1", "-1 7 -1", "", "0 1", "", "", "11", "", "", "", ""});
        Tags.artistTags.put("华语男", new String[]{"", "1 7 -1", "", "1 1", "", "", "", "1 ", "huayu-nan", "", ""});
        Tags.artistTags.put("华语女", new String[]{"", "2 7 -1", "", "2 1", "", "", "", "2 ", "huayu-nv", "", ""});
        Tags.artistTags.put("华语组合", new String[]{"", "3 7 -1", "", "3 1", "", "", "", "3 ", "huayu-group", "", ""});
        Tags.artistTags.put("内地", new String[]{"", "", "", "", "", "-100 -100 -100 200", "", "", "", " 内地 ", ""});
        Tags.artistTags.put("内地男", new String[]{"", "", "", "", "", "0 -100 -100 200", "", "", "", " 内地 男", ""});
        Tags.artistTags.put("内地女", new String[]{"", "", "", "", "", "1 -100 -100 200", "", "", "", " 内地 女", ""});
        Tags.artistTags.put("内地组合", new String[]{"", "", "", "", "", "2 -100 -100 200", "", "", "", " 内地 组合", ""});
        Tags.artistTags.put("内地乐队", new String[]{"", "", "", "", "", "", "", "", "", " 内地 乐队", ""});
        Tags.artistTags.put("港台", new String[]{"", "", "", "", "", "-100 -100 -100 2", "", "", "", " 港台 ", ""});
        Tags.artistTags.put("港台男", new String[]{"", "", "", "", "", "0 -100 -100 2", "", "", "", " 港台 男", ""});
        Tags.artistTags.put("港台女", new String[]{"", "", "", "", "", "1 -100 -100 2", "", "", "", " 港台 女", ""});
        Tags.artistTags.put("港台组合", new String[]{"", "", "", "", "", "2 -100 -100 2", "", "", "", " 港台 组合", ""});
        Tags.artistTags.put("港台乐队", new String[]{"", "", "", "", "", "", "", "", "", " 港台 乐队", ""});
        Tags.artistTags.put("欧美", new String[]{"2", "-1 96 -1", "", "0 2", "", "-100 -100 -100 5", "13", "", "", " 欧美 ", ""});
        Tags.artistTags.put("欧美男", new String[]{"", "1 96 -1", "", "1 2", "", "0 -100 -100 5", "", "7 ", "oumei-nan", " 欧美 男", ""});
        Tags.artistTags.put("欧美女", new String[]{"", "2 96 -1", "", "2 2", "", "1 -100 -100 5", "", "8 ", "oumei-nv", " 欧美 女", ""});
        Tags.artistTags.put("欧美组合", new String[]{"", "3 96 -1", "", "3 2", "", "2 -100 -100 5", "", "9 ", "oumei-group", " 欧美 组合", ""});
        Tags.artistTags.put("欧美乐队", new String[]{"", "", "", "", "", "", "", "", "", " 欧美 乐队", ""});
        Tags.artistTags.put("韩国", new String[]{"3", "-1 16 -1", "", "0 6", "", "-100 -100 -100 3", "", "", "", " 韩国 ", ""});
        Tags.artistTags.put("韩国男", new String[]{"", "1 16 -1", "", "1 6", "", "0 -100 -100 3", "", "", "", " 韩国 男", ""});
        Tags.artistTags.put("韩国女", new String[]{"", "2 16 -1", "", "2 6", "", "1 -100 -100 3", "", "", "", " 韩国 女", ""});
        Tags.artistTags.put("韩国组合", new String[]{"", "3 16 -1", "", "3 6", "", "2 -100 -100 3", "", "", "", " 韩国 组合", ""});
        Tags.artistTags.put("韩国乐队", new String[]{"", "", "", "", "", "", "", "", "", " 韩国 乐队", ""});
        Tags.artistTags.put("日本", new String[]{"4", "-1 8 -1", "", "0 5", "", "-100 -100 -100 4", "", "", "", " 日本 ", ""});
        Tags.artistTags.put("日本男", new String[]{"", "1 8 -1", "", "1 5", "", "0 -100 -100 4", "", "", "", " 日本 男", ""});
        Tags.artistTags.put("日本女", new String[]{"", "2 8 -1", "", "2 5", "", "1 -100 -100 4", "", "", "", " 日本 女", ""});
        Tags.artistTags.put("日本组合", new String[]{"", "3 8 -1", "", "3 5", "", "2 -100 -100 4", "", "", "", " 日本 组合", ""});
        Tags.artistTags.put("日本乐队", new String[]{"", "", "", "", "", "", "", "", "", " 日本 乐队", ""});
        Tags.artistTags.put("日韩", new String[]{"", "", "", "0 3", "", "", "12", "", "", "", ""});
        Tags.artistTags.put("日韩男", new String[]{"", "", "", "1 3", "", "", "", "4 ", "rihan-nan", "", ""});
        Tags.artistTags.put("日韩女", new String[]{"", "", "", "2 3", "", "", "", "5 ", "rihan-nv", "", ""});
        Tags.artistTags.put("日韩组合", new String[]{"", "", "", "3 3", "", "", "", "6 ", "rihan-group", "", ""});
        Tags.artistTags.put("其他", new String[]{"", "-1 0 -1", "", "0 4", "", "-100 -100 -100 6", "", "10 ", "", " 其他 ", ""});
        Tags.artistTags.put("其他男", new String[]{"", "1 0 -1", "", "1 4", "", "0 -100 -100 6", "", "", "", " 其他 男", ""});
        Tags.artistTags.put("其他女", new String[]{"", "2 0 -1", "", "2 4", "", "1 -100 -100 6", "", "", "", " 其他 女", ""});
        Tags.artistTags.put("其他组合", new String[]{"", "3 0 -1", "", "3 4", "", "2 -100 -100 6", "", "", "", " 其他 组合", ""});
        Tags.artistTags.put("其他乐队", new String[]{"", "", "", "", "", "", "", "", "", " 其他 乐队", ""});

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(() -> NcArtistTagReq.getInstance().initStyleArtistTag());
        executor.submit(() -> KgArtistTagReq.getInstance().initIpTag());
        executor.submit(() -> QqArtistTagReq.getInstance().initArtistTag());
        executor.await();
    }
}
