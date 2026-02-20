package net.doge.constant.service.tag;

/**
 * 资源标签类型，用于区分某个 API 使用
 */
public class TagType {
    // 音乐搜索
    // 节目搜索 (猫耳)
    public static final int PROGRAM_SEARCH_ME = 0;

    // 推荐歌单
    // 曲风歌单 (网易云)
    public static final int STYLE_PLAYLIST_NC = 0;
    // 推荐歌单(推荐) (酷狗)
    public static final int RECOMMEND_CAT_PLAYLIST_KG = 1;
    // 分类推荐歌单(最新) (QQ)
    public static final int NEW_PLAYLIST_QQ = 2;
    // 分类歌单(最新) (猫耳)
    public static final int NEW_PLAYLIST_ME = 3;
    // 分类歌单 (5sing)
    public static final int NEW_PLAYLIST_FS = 4;

    // 精品歌单
    // 精品歌单 (网易云)
    public static final int HIGH_QUALITY_PLAYLIST_NC = 0;
    // 网友精选碟 (网易云)
    public static final int PICKED_PLAYLIST_NC = 1;
    // Top 歌单 (酷狗)
    public static final int TOP_PLAYLIST_KG = 2;
    // 编辑精选歌单 (酷狗)
    public static final int IP_PLAYLIST_KG = 3;
    // 分类歌单 (QQ)
    public static final int CAT_PLAYLIST_QQ = 4;
    // 分类歌单 (酷我)
    public static final int CAT_PLAYLIST_KW = 5;
    // 分类歌单 (咪咕)
    public static final int CAT_PLAYLIST_MG = 6;
    // 分类歌单 (千千)
    public static final int CAT_PLAYLIST_QI = 7;
    // 分类歌单 (猫耳)
    public static final int CAT_PLAYLIST_ME = 8;
    // 探索歌单 (猫耳)
    public static final int EXP_PLAYLIST_ME = 9;
    // 分类歌单 (5sing)
    public static final int HOT_PLAYLIST_FS = 10;

    // 热门歌曲
    // 曲风歌曲 (网易云)
    public static final int STYLE_HOT_SONG_NC = 0;
    // 歌曲推荐 (酷狗)
    public static final int CARD_SONG_KG = 1;
    // 主题歌曲 (酷狗)
    public static final int THEME_SONG_KG = 2;
    // 频道歌曲 (酷狗)
    public static final int FM_SONG_KG = 3;
    // 编辑精选歌曲 (酷狗)
    public static final int IP_SONG_KG = 4;
    // 热门歌曲 (音乐磁场)
    public static final int HOT_MUSIC_HF = 5;
    // 热门歌曲 (咕咕咕音乐)
    public static final int HOT_MUSIC_GG = 6;

    // 新歌速递
    // 新歌速递 (网易云)
    public static final int FAST_NEW_SONG_NC = 0;
    // 曲风歌曲 (网易云)
    public static final int STYLE_NEW_SONG_NC = 1;
    // 推荐新歌 (酷狗)
    public static final int RECOMMEND_NEW_SONG_KG = 2;
    // 风格歌曲 (酷狗)
    public static final int STYLE_SONG_KG = 3;
    // 推荐新歌 (QQ)
    public static final int RECOMMEND_NEW_SONG_QQ = 4;
    // 推荐新歌 (音乐磁场)
    public static final int RECOMMEND_NEW_SONG_HF = 5;
    // 推荐新歌 (咕咕咕音乐)
    public static final int RECOMMEND_NEW_SONG_GG = 6;
    // 推荐新歌 (5sing)
    public static final int RECOMMEND_NEW_SONG_FS = 7;

    // 新碟上架
    // 新碟上架 (网易云)
    public static final int NEW_ALBUM_NC = 0;
    // 数字专辑语种风格馆 (网易云)
    public static final int LANG_DI_ALBUM_NC = 1;
    // 曲风专辑 (网易云)
    public static final int STYLE_ALBUM_NC = 2;
    // 新碟上架 (酷狗)
    public static final int NEW_ALBUM_KG = 3;
    // 编辑精选专辑 (酷狗)
    public static final int IP_ALBUM_KG = 4;
    // 新碟上架 (QQ)
    public static final int NEW_ALBUM_QQ = 5;
    // 分类专辑 (豆瓣)
    public static final int CAT_ALBUM_DB = 6;
    // 分类专辑 (堆糖)
    public static final int CAT_ALBUM_DT = 7;

    // 歌手排行
    // 歌手榜 (网易云)
    public static final int ARTIST_RANK_LIST_NC = 0;
    // 分类歌手 (网易云)
    public static final int CAT_ARTIST_NC = 1;
    // 曲风歌手 (网易云)
    public static final int STYLE_ARTIST_NC = 2;
    // 热门歌手 (酷狗)
    public static final int HOT_ARTIST_LIST_KG = 3;
    // 编辑精选歌手 (酷狗)
    public static final int IP_ARTIST_KG = 4;
    // 歌手榜单 (QQ)
    public static final int ARTIST_RANK_QQ = 5;
    // 推荐歌手 (酷我)
    public static final int ARTIST_LIST_KW = 6;
    // 全部歌手 (酷我)
    public static final int ALL_ARTIST_KW = 7;
    // 分类歌手 (咪咕)
    public static final int CAT_ARTIST_MG = 8;
    // 分类歌手 (千千)
    public static final int CAT_ARTIST_QI = 9;
    // 分类声优 (猫耳)
    public static final int CAT_CV_ME = 10;

    // 热门电台
    // 分类热门电台 (网易云)
    public static final int CAT_HOT_RADIO_NC = 0;
    // 分类推荐电台 (网易云)
    public static final int CAT_REC_RADIO_NC = 1;
    // 分类电台 (喜马拉雅)
    public static final int CAT_RADIO_XM = 2;
    // 频道电台 (喜马拉雅)
    public static final int CHANNEL_RADIO_XM = 3;
    //    // 频道电台 (喜马拉雅)
//    public static final int CHANNEL_RADIO_XM = 4;
    // 分类电台 (猫耳)
    public static final int CAT_RADIO_ME = 5;
    // 分类电台 (豆瓣)
    public static final int CAT_RADIO_DB = 6;
    // 分类游戏电台 (豆瓣)
    public static final int CAT_GAME_RADIO_DB = 7;
    // 分类电台 (咪咕)
    public static final int CAT_RADIO_MG = 8;

    // 推荐节目
    // 探索节目 (猫耳)
    public static final int EXP_PROGRAM_ME = 0;
    // 首页分类节目 (猫耳)
    public static final int INDEX_CAT_PROGRAM_ME = 1;

    // 推荐 MV
    // MV 排行 (网易云)
    public static final int MV_RANK_NC = 0;
    // 全部 MV (网易云)
    public static final int ALL_MV_NC = 1;
    // 推荐 MV (酷狗)
    public static final int RECOMMEND_MV_KG = 2;
    // 编辑精选 MV (酷狗)
    public static final int IP_MV_KG = 3;
    // 推荐 MV (QQ)
    public static final int RECOMMEND_MV_QQ = 4;
    // 推荐 MV (QQ)
    public static final int RECOMMEND_MV_QQ_2 = 5;
    // 最新 MV (QQ)
    public static final int NEW_MV_QQ = 6;
    // 推荐 MV (酷我)
    public static final int RECOMMEND_MV_KW = 7;
    // 推荐视频 (好看)
    public static final int RECOMMEND_VIDEO_HK = 8;
    // 分区排行榜视频 (哔哩哔哩)
    public static final int CAT_RANK_VIDEO_BI = 9;
    // 视频 (发姐)
    public static final int VIDEO_FA = 10;
    // 视频 (李志)
    public static final int VIDEO_LZ = 11;
}
