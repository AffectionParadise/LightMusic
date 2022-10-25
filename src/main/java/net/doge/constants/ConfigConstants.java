package net.doge.constants;

/**
 * @Author yzx
 * @Description 配置属性类
 * @Date 2020/12/15
 */
public class ConfigConstants {
    // 配置文件名称
    public final static String fileName = "config.json";

    // 所有自定义风格
    public final static String CUSTOM_UI_STYLES = "customUIStyles";
    // 当前 UI 风格索引
    public final static String CURR_UI_STYLE = "currUIStyle";
    // 关闭窗口操作
    public final static String CLOSE_WINDOW_OPTION = "closeWindowOption";
    // 播放视频是否关闭主界面
    public final static String VIDEO_ONLY = "videoOnly";
    // 播放模式
    public final static String PLAY_MODE = "playMode";
    // 歌曲下载路径
    public final static String MUSIC_DOWN_PATH = "musicDownPath";
    // MV 下载路径
    public final static String MV_DOWN_PATH = "mvDownPath";
    // 缓存路径
    public final static String CACHE_PATH = "cachePath";
    // 最大缓存大小
    public final static String MAX_CACHE_SIZE = "maxCacheSize";
    // 最大播放历史数量
    public final static String MAX_HISTORY_COUNT = "maxHistoryCount";
    // 最大搜索历史数量
    public final static String MAX_SEARCH_HISTORY_COUNT = "maxSearchHistoryCount";
    // 最大同时下载的任务数
    public final static String MAX_CONCURRENT_TASK_COUNT = "maxConcurrentTaskCount";
    // 是否显示频谱
    public final static String SHOW_SPECTRUM = "showSpectrum";
    // 是否碟片虚化
    public final static String IS_BLUR = "isBlur";
    // 是否自动下载歌词
    public final static String AUTO_DOWNLOAD_LYRIC = "autoDownloadLyric";
    // 是否显示桌面歌词
    public final static String SHOW_DESKTOP_LYRIC = "showDesktopLyric";
    // 是否锁定桌面歌词
    public final static String LOCK_DESKTOP_LYRIC = "desktopLyricLocked";
    // 桌面歌词坐标
    public final static String DESKTOP_LYRIC_X = "desktopLyricX";
    public final static String DESKTOP_LYRIC_Y = "desktopLyricY";
    // 是否桌面歌词置顶
    public final static String DESKTOP_LYRIC_ON_TOP = "desktopLyricOnTop";
    // 桌面歌词透明度
    public final static String DESKTOP_LYRIC_ALPHA = "desktopLyricAlpha";
    // 快进/快退时间
    public final static String FOB_TIME = "fobTime";
    // 视频快进/快退时间
    public final static String VIDEO_FOB_TIME = "videoFobTime";
    // 速率
    public final static String RATE = "rate";
    // 视频速率
    public final static String VIDEO_RATE = "videoRate";
    // 频谱样式
    public final static String SPECTRUM_STYLE = "spectrumStyle";
    // 均衡
    public final static String BALANCE = "balance";
    // 是否静音
    public final static String MUTE = "isMute";
    // 音量
    public final static String VOLUME = "volume";
    // 音效名称
    public final static String SOUND_EFFECT_NAME = "soundEffectName";
    // 均衡数据
    public final static String EQUALIZER_DATA = "equalizerData";
    // 迷你窗口 x
    public final static String MINIX = "miniX";
    // 迷你窗口 y
    public final static String MINIY = "miniY";
    // 中文类型
    public final static String CHINESE_TYPE = "chineseType";
    // 日文类型
    public final static String JAPANESE_TYPE = "japaneseType";
    // 歌词类型
    public final static String LYRIC_TYPE = "lyricType";
    // 排序顺序
    public final static String SORT_ORDER = "sortOrder";
    // 当前所有歌曲目录
    public final static String CATALOGS = "catalogs";
    // 当前播放列表
    public final static String MUSIC_LIST = "musicList";
    // 当前播放历史
    public final static String HISTORY = "history";
    // 当前收藏列表
    public final static String COLLECTION = "collection";
    // 当前歌单收藏列表
    public final static String PLAYLIST_COLLECTION = "playlistCollection";
    // 当前专辑收藏列表
    public final static String ALBUM_COLLECTION = "albumCollection";
    // 当前歌手收藏列表
    public final static String ARTIST_COLLECTION = "artistCollection";
    // 当前电台收藏列表
    public final static String RADIO_COLLECTION = "radioCollection";
    // 当前 MV 收藏列表
    public final static String MV_COLLECTION = "mvCollection";
    // 当前榜单收藏列表
    public final static String RANKING_COLLECTION = "rankingCollection";
    // 当前用户收藏列表
    public final static String USER_COLLECTION = "userCollection";
    // 当前下载任务列表
    public final static String TASKS = "tasks";
    // 当前播放队列
    public final static String PLAY_QUEUE = "playQueue";
    // 当前播放歌曲
    public final static String MUSIC_PLAYING = "musicPlaying";

    // 在线音乐来源
    public final static String NET_MUSIC_SOURCE = "source";
    // 在线音乐格式
    public final static String NET_MUSIC_FORMAT = "format";
    // 在线音乐 hash
    public final static String NET_MUSIC_HASH = "hash";
    // 在线音乐 id
    public final static String NET_MUSIC_ID = "id";
    // 在线音乐节目 id
    public final static String NET_MUSIC_PROGRAM_ID = "programId";
    // 在线音乐曲名
    public final static String NET_MUSIC_NAME = "name";
    // 在线音乐艺术家
    public final static String NET_MUSIC_ARTIST = "artist";
    // 在线音乐艺术家 id
    public final static String NET_MUSIC_ARTIST_ID = "artistId";
    // 在线音乐时长
    public final static String NET_MUSIC_DURATION = "duration";
    // 在线音乐专辑名称
    public final static String NET_MUSIC_ALBUM_NAME = "albumName";
    // 在线音乐专辑 id
    public final static String NET_MUSIC_ALBUM_ID = "albumId";
    // 在线音乐 MV ID
    public final static String NET_MUSIC_MV_ID = "mvId";

    // 歌单来源
    public final static String NET_PLAYLIST_SOURCE = "source";
    // 歌单 id
    public final static String NET_PLAYLIST_ID = "id";
    // 歌单名称
    public final static String NET_PLAYLIST_NAME = "name";
    // 歌单创建者
    public final static String NET_PLAYLIST_CREATOR = "creator";
    // 歌单创建者 id
    public final static String NET_PLAYLIST_CREATOR_ID = "creatorId";
    // 歌单封面缩略图 url
    public final static String NET_PLAYLIST_COVER_IMG_THUMB_URL = "coverImgThumbUrl";
    // 歌单歌曲数量
    public final static String NET_PLAYLIST_TRACK_COUNT = "trackCount";
    // 歌单播放量
    public final static String NET_PLAYLIST_PLAY_COUNT = "playCount";

    // 专辑来源
    public final static String NET_ALBUM_SOURCE = "source";
    // 专辑 id
    public final static String NET_ALBUM_ID = "id";
    // 专辑名称
    public final static String NET_ALBUM_NAME = "name";
    // 专辑艺术家
    public final static String NET_ALBUM_ARTIST = "artist";
    // 专辑封面缩略图 url
    public final static String NET_ALBUM_COVER_IMG_THUMB_URL = "coverImgThumbUrl";
    // 专辑歌曲数量
    public final static String NET_ALBUM_SONG_NUM = "songNum";
    // 专辑发布时间
    public final static String NET_ALBUM_PUBLISH_TIME = "publishTime";

    // 歌手来源
    public final static String NET_ARTIST_SOURCE = "source";
    // 歌手 id
    public final static String NET_ARTIST_ID = "id";
    // 歌手名称
    public final static String NET_ARTIST_NAME = "name";
    // 歌手封面 url
    public final static String NET_ARTIST_COVER_IMG_URL = "coverImgUrl";
    // 歌手封面缩略图 url
    public final static String NET_ARTIST_COVER_IMG_THUMB_URL = "coverImgThumbUrl";
    // 歌手歌曲数量
    public final static String NET_ARTIST_SONG_NUM = "songNum";
    // 歌手专辑数量
    public final static String NET_ARTIST_ALBUM_NUM = "albumNum";
    // 歌手 MV 数量
    public final static String NET_ARTIST_MV_NUM = "mvNum";

    // 电台来源
    public final static String NET_RADIO_SOURCE = "source";
    // 电台 id
    public final static String NET_RADIO_ID = "id";
    // 电台名称
    public final static String NET_RADIO_NAME = "name";
    // 电台封面 url
    public final static String NET_RADIO_COVER_IMG_URL = "coverImgUrl";
    // 电台封面缩略图 url
    public final static String NET_RADIO_COVER_IMG_THUMB_URL = "coverImgThumbUrl";
    // 电台 DJ
    public final static String NET_RADIO_DJ = "dj";
    // 电台 DJ id
    public final static String NET_RADIO_DJ_ID = "djId";
    // 电台类型
    public final static String NET_RADIO_CATEGORY = "category";
    // 电台节目数量
    public final static String NET_RADIO_TRACK_COUNT = "trackCount";
    // 电台播放量
    public final static String NET_RADIO_PLAY_COUNT = "playCount";

    // MV 来源
    public final static String NET_MV_SOURCE = "source";
    // MV 类型
    public final static String NET_MV_TYPE = "type";
    // MV 格式
    public final static String NET_MV_FORMAT = "format";
    // MV id
    public final static String NET_MV_ID = "id";
    // MV bvid
    public final static String NET_MV_BVID = "bvid";
    // MV 名
    public final static String NET_MV_NAME = "name";
    // MV 艺术家
    public final static String NET_MV_ARTIST = "artist";
    // MV 发布者 id
    public final static String NET_MV_CREATOR_ID = "creatorId";
    // MV 时长
    public final static String NET_MV_DURATION = "duration";
    // MV 发布时间
    public final static String NET_MV_PUB_TIME = "pubTime";
    // MV 封面图片 url
    public final static String NET_MV_COVER_IMG_URL = "coverImgUrl";
    // MV 播放量
    public final static String NET_MV_PLAY_COUNT = "playCount";

    // 榜单来源
    public final static String NET_RANKING_SOURCE = "source";
    // 榜单id
    public final static String NET_RANKING_ID = "id";
    // 榜单名称
    public final static String NET_RANKING_NAME = "name";
    // 榜单描述
    public final static String NET_RANKING_DESCRIPTION = "description";
    // 榜单播放量
    public final static String NET_RANKING_PLAY_COUNT = "playCount";
    // 榜单更新频率
    public final static String NET_RANKING_UPDATE_FRE = "updateFre";
    // 榜单更新时间
    public final static String NET_RANKING_UPDATE_TIME = "updateTime";
    // 榜单封面图片 url
    public final static String NET_RANKING_COVER_IMG_URL = "coverImgUrl";

    // 用户来源
    public final static String NET_USER_SOURCE = "source";
    // 用户是否是声优
    public final static String NET_USER_IS_CV = "isCV";
    // 用户 id
    public final static String NET_USER_ID = "id";
    // 用户名称
    public final static String NET_USER_NAME = "name";
    // 用户性别
    public final static String NET_USER_GENDER = "gender";
    // 用户头像 url
    public final static String NET_USER_AVATAR_URL = "avatarUrl";
    // 用户头像缩略图 url
    public final static String NET_USER_AVATAR_THUMB_URL = "avatarThumbUrl";
    // 用户关注数
    public final static String NET_USER_FOLLOW = "follow";
    // 用户粉丝数
    public final static String NET_USER_FOLLOWED = "followed";
    // 用户歌单数
    public final static String NET_USER_PLAYLIST_COUNT = "playlistCount";
    // 用户电台数
    public final static String NET_USER_RADIO_COUNT = "radioCount";
    // 用户节目数
    public final static String NET_USER_PROGRAM_COUNT = "programCount";

    // 在线音乐搜索历史
    public final static String NET_MUSIC_HISTORY_SEARCH = "historySearch";
    // 歌单搜索历史
    public final static String NET_PLAYLIST_HISTORY_SEARCH = "playlistHistorySearch";
    // 专辑搜索历史
    public final static String NET_ALBUM_HISTORY_SEARCH = "albumHistorySearch";
    // 歌手搜索历史
    public final static String NET_ARTIST_HISTORY_SEARCH = "artistHistorySearch";
    // 电台搜索历史
    public final static String NET_RADIO_HISTORY_SEARCH = "radioHistorySearch";
    // MV 搜索历史
    public final static String NET_MV_HISTORY_SEARCH = "mvHistorySearch";
    // 用户搜索历史
    public final static String NET_USER_HISTORY_SEARCH = "userHistorySearch";

    // 任务 url
    public final static String TASK_URL = "url";
    // 任务文件路径
    public final static String TASK_DEST = "dest";
    // 任务类型
    public final static String TASK_TYPE = "type";
    // 任务的音乐信息
    public final static String TASK_MUSIC_INFO = "musicInfo";
    // 任务的 MV 信息
    public final static String TASK_MV_INFO = "mvInfo";
    // 任务名称
    public final static String TASK_NAME = "name";
    // 任务状态
    public final static String TASK_STATUS = "status";
    // 任务已完成大小
    public final static String TASK_FINISHED = "finished";
    // 任务总大小
    public final static String TASK_TOTAL = "total";
}
