package net.doge.constant.config;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.system.LMDataConstants;
import net.doge.util.lmdata.LMDataUtil;

/**
 * @Author Doge
 * @Description 配置属性类
 * @Date 2020/12/15
 */
public class ConfigConstants {
    // 配置文件名称
    public static final String CONFIG_FILE_NAME = "config" + LMDataConstants.DATA_FILE_SUFFIX;
    // 配置文件数据
    public static final JSONObject CONFIG_DATA = LMDataUtil.read(CONFIG_FILE_NAME);

    // 所有自定义主题
    public static final String CUSTOM_UI_STYLES = "customUIStyles";
    // 当前 UI 主题索引
    public static final String CURR_UI_STYLE = "currUIStyle";
    // 是否启用快捷键
    public static final String KEY_ENABLED = "keyEnabled";
    public static final String PLAY_OR_PAUSE_KEYS = "playOrPauseKeys";
    public static final String PLAY_LAST_KEYS = "playLastKeys";
    public static final String PLAY_NEXT_KEYS = "playNextKeys";
    public static final String BACKWARD_KEYS = "backwardKeys";
    public static final String FORWARD_KEYS = "forwardKeys";
    public static final String VIDEO_FULL_SCREEN_KEYS = "videoFullScreenKeys";
    // 选项卡
    public static final String TAB_INDEX = "tabIndex";
    // 个人音乐选项卡
    public static final String PERSONAL_TAB_INDEX = "personalTabIndex";
    // 收藏选项卡
    public static final String COLLECTION_TAB_INDEX = "collectionTabIndex";
    // 推荐选项卡
    public static final String RECOMMEND_TAB_INDEX = "recommendTabIndex";
    // 是否自动更新
    public static final String AUTO_UPDATE = "autoUpdate";
    // 界面语言
    public static final String UI_LANGUAGE = "uiLanguage";
    // 界面字体
    public static final String UI_FONT = "uiFont";
    // 关闭窗口操作
    public static final String CLOSE_WINDOW_OPTION = "closeWindowOption";
    // 窗口大小
    public static final String WINDOW_SIZE = "windowSize";
    // 是否显示已播放时间
    public static final String TIME_ELAPSED_MODE = "timeElapsedMode";
    public static final String VIDEO_TIME_ELAPSED_MODE = "videoTimeElapsedMode";
    // 播放视频是否关闭主界面
    public static final String VIDEO_ONLY = "videoOnly";
    // 是否显示侧边栏文字
    public static final String SHOW_TAB_TEXT = "showTabText";
    // 歌词对齐方式
    public static final String LRC_ALIGNMENT = "lrcAlignment";
    // 频谱最大高度
    public static final String SPEC_MAX_HEIGHT = "specMaxHeight";
    // 高斯模糊因子
    public static final String GS_FACTOR_INDEX = "gsFactorIndex";
    // 暗角滤镜因子
    public static final String DARKER_FACTOR_INDEX = "darkerFactorIndex";
    // 线性渐变色彩风格
    public static final String GRADIENT_COLOR_STYLE_INDEX = "gradientColorStyleIndex";
    // 播放模式
    public static final String PLAY_MODE = "playMode";
    // 歌曲下载路径
    public static final String MUSIC_DOWN_PATH = "musicDownPath";
    // MV 下载路径
    public static final String MV_DOWN_PATH = "mvDownPath";
    // 缓存路径
    public static final String CACHE_PATH = "cachePath";
    // 最大缓存大小
    public static final String MAX_CACHE_SIZE = "maxCacheSize";
    // 最大播放历史数量
    public static final String MAX_HISTORY_COUNT = "maxHistoryCount";
    // 最大搜索历史数量
    public static final String MAX_SEARCH_HISTORY_COUNT = "maxSearchHistoryCount";
    // 最大同时下载的任务数
    public static final String MAX_CONCURRENT_TASK_COUNT = "maxConcurrentTaskCount";
    // 是否显示频谱
    public static final String SHOW_SPECTRUM = "showSpectrum";
    // 是否高斯模糊
    public static final String GS_ON = "gsOn";
    // 是否暗化
    public static final String DARKER_ON = "darkerOn";
    // 是否朦胧遮罩
    public static final String MASK_ON = "maskOn";
    // 是否律动
    public static final String GROOVE_ON = "grooveOn";
    // 模糊类型
    public static final String BLUR_TYPE = "blurType";
    // 是否自动下载歌词
    public static final String AUTO_DOWNLOAD_LYRIC = "autoDownloadLyric";
    //    // 是否添加逐字时间轴
//    public static final String VERBATIM_TIMELINE = "verbatimTimeline";
    // 歌词偏移
    public static final String LYRIC_OFFSET = "lyricOffset";
    // 频谱透明度
    public static final String SPEC_OPACITY = "spectrumOpacity";
    // 是否显示桌面歌词
    public static final String SHOW_DESKTOP_LYRIC = "showDesktopLyric";
    // 是否锁定桌面歌词
    public static final String LOCK_DESKTOP_LYRIC = "desktopLyricLocked";
    // 桌面歌词坐标
    public static final String DESKTOP_LYRIC_X = "desktopLyricX";
    public static final String DESKTOP_LYRIC_Y = "desktopLyricY";
    // 是否桌面歌词置顶
    public static final String DESKTOP_LYRIC_ON_TOP = "desktopLyricOnTop";
    // 桌面歌词透明度
    public static final String DESKTOP_LYRIC_ALPHA = "desktopLyricAlpha";
    // 桌面歌词字体大小
    public static final String DESKTOP_LYRIC_FONT_SIZE = "desktopLyricFontSize";
    // 音质
    public static final String AUDIO_QUALITY = "audioQuality";
    // 画质
    public static final String VIDEO_QUALITY = "videoQuality";
    // 快进/快退时间
    public static final String FOB_TIME = "fobTime";
    // 视频快进/快退时间
    public static final String VIDEO_FOB_TIME = "videoFobTime";
    // 速率
    public static final String RATE = "rate";
    // 视频速率
    public static final String VIDEO_RATE = "videoRate";
    // 频谱样式
    public static final String SPECTRUM_STYLE = "spectrumStyle";
    // 均衡
    public static final String BALANCE = "balance";
    // 是否静音
    public static final String MUTE = "isMute";
    // 音量
    public static final String VOLUME = "volume";
    // 音效
    public static final String SOUND_EFFECT = "soundEffect";
    // 均衡数据
    public static final String EQUALIZER_DATA = "equalizerData";
    // 迷你窗口 x
    public static final String MINIX = "miniX";
    // 迷你窗口 y
    public static final String MINIY = "miniY";
    // 歌词类型
    public static final String LYRIC_TYPE = "lyricType";
    // 排序顺序
    public static final String SORT_ORDER = "sortOrder";
    // 当前所有歌曲目录
    public static final String CATALOGS = "catalogs";
    // 当前播放列表
//    public static final String MUSIC_LIST = "musicList";
    // 当前播放历史
    public static final String HISTORY = "history";
    // 当前收藏列表
//    public static final String COLLECTION = "collection";
    // 当前歌单收藏列表
    public static final String PLAYLIST_COLLECTION = "playlistCollection";
    // 当前专辑收藏列表
    public static final String ALBUM_COLLECTION = "albumCollection";
    // 当前歌手收藏列表
    public static final String ARTIST_COLLECTION = "artistCollection";
    // 当前电台收藏列表
    public static final String RADIO_COLLECTION = "radioCollection";
    // 当前 MV 收藏列表
    public static final String MV_COLLECTION = "mvCollection";
    // 当前榜单收藏列表
    public static final String RANKING_COLLECTION = "rankingCollection";
    // 当前用户收藏列表
    public static final String USER_COLLECTION = "userCollection";
    // 当前下载任务列表
    public static final String TASKS = "tasks";
    // 当前播放队列
    public static final String PLAY_QUEUE = "playQueue";
    // 当前播放歌曲
    public static final String CURR_SONG = "currSong";

    // 收藏夹
    public static final String LOCAL_PLAYLISTS = "localPlaylists";
    public static final String COLLECTION_PLAYLISTS = "collectionPlaylists";
    // 收藏夹名称
    public static final String LOCAL_PLAYLIST_NAME = "localPlaylistName";
    public static final String COLLECTION_PLAYLIST_NAME = "collectionPlaylistName";
    // 收藏夹是否默认
    public static final String LOCAL_PLAYLIST_IS_DEFAULT = "localPlaylistIsDefault";
    public static final String COLLECTION_PLAYLIST_IS_DEFAULT = "collectionPlaylistIsDefault";
    // 收藏夹音乐列表
    public static final String LOCAL_PLAYLIST_MUSIC_LIST = "localPlaylistMusicList";
    public static final String COLLECTION_PLAYLIST_MUSIC_LIST = "collectionPlaylistMusicList";

    // 在线音乐来源
    public static final String NET_MUSIC_SOURCE = "source";
    // 在线音乐 hash
    public static final String NET_MUSIC_HASH = "hash";
    // 在线音乐 id
    public static final String NET_MUSIC_ID = "id";
    // 在线音乐节目 id
    public static final String NET_MUSIC_PROGRAM_ID = "programId";
    // 在线音乐曲名
    public static final String NET_MUSIC_NAME = "name";
    // 在线音乐艺术家
    public static final String NET_MUSIC_ARTIST = "artist";
    // 在线音乐艺术家 id
    public static final String NET_MUSIC_ARTIST_ID = "artistId";
    // 在线音乐时长
    public static final String NET_MUSIC_DURATION = "duration";
    // 在线音乐专辑名称
    public static final String NET_MUSIC_ALBUM_NAME = "albumName";
    // 在线音乐专辑 id
    public static final String NET_MUSIC_ALBUM_ID = "albumId";
    // 在线音乐 MV ID
    public static final String NET_MUSIC_MV_ID = "mvId";
    // 在线音乐音质类型
    public static final String NET_MUSIC_QUALITY_TYPE = "qualityType";

    // 歌单来源
    public static final String NET_PLAYLIST_SOURCE = "source";
    // 歌单 id
    public static final String NET_PLAYLIST_ID = "id";
    // 歌单名称
    public static final String NET_PLAYLIST_NAME = "name";
    // 歌单创建者
    public static final String NET_PLAYLIST_CREATOR = "creator";
    // 歌单创建者 id
    public static final String NET_PLAYLIST_CREATOR_ID = "creatorId";
    // 歌单封面缩略图 url
    public static final String NET_PLAYLIST_COVER_IMG_THUMB_URL = "coverImgThumbUrl";
    // 歌单歌曲数量
    public static final String NET_PLAYLIST_TRACK_COUNT = "trackCount";
    // 歌单播放量
    public static final String NET_PLAYLIST_PLAY_COUNT = "playCount";

    // 专辑来源
    public static final String NET_ALBUM_SOURCE = "source";
    // 专辑 id
    public static final String NET_ALBUM_ID = "id";
    // 专辑名称
    public static final String NET_ALBUM_NAME = "name";
    // 专辑艺术家
    public static final String NET_ALBUM_ARTIST = "artist";
    // 专辑艺术家 id
    public static final String NET_ALBUM_ARTIST_ID = "artistId";
    // 专辑封面缩略图 url
    public static final String NET_ALBUM_COVER_IMG_THUMB_URL = "coverImgThumbUrl";
    // 专辑歌曲数量
    public static final String NET_ALBUM_SONG_NUM = "songNum";
    // 专辑发布时间
    public static final String NET_ALBUM_PUBLISH_TIME = "publishTime";

    // 歌手来源
    public static final String NET_ARTIST_SOURCE = "source";
    // 是否是社团
    public static final String NET_ARTIST_IS_ORGANIZATION = "isOrganization";
    // 歌手 id
    public static final String NET_ARTIST_ID = "id";
    // 歌手名称
    public static final String NET_ARTIST_NAME = "name";
    // 歌手封面 url
    public static final String NET_ARTIST_COVER_IMG_URL = "coverImgUrl";
    // 歌手封面缩略图 url
    public static final String NET_ARTIST_COVER_IMG_THUMB_URL = "coverImgThumbUrl";
    // 歌手歌曲数量
    public static final String NET_ARTIST_SONG_NUM = "songNum";
    // 歌手专辑数量
    public static final String NET_ARTIST_ALBUM_NUM = "albumNum";
    // 歌手 MV 数量
    public static final String NET_ARTIST_MV_NUM = "mvNum";

    // 电台来源
    public static final String NET_RADIO_SOURCE = "source";
    // 电台 id
    public static final String NET_RADIO_ID = "id";
    // 电台名称
    public static final String NET_RADIO_NAME = "name";
    // 电台封面 url
    public static final String NET_RADIO_COVER_IMG_URL = "coverImgUrl";
    // 电台封面缩略图 url
    public static final String NET_RADIO_COVER_IMG_THUMB_URL = "coverImgThumbUrl";
    // 电台 DJ
    public static final String NET_RADIO_DJ = "dj";
    // 电台 DJ id
    public static final String NET_RADIO_DJ_ID = "djId";
    // 电台类型
    public static final String NET_RADIO_CATEGORY = "category";
    // 电台节目数量
    public static final String NET_RADIO_TRACK_COUNT = "trackCount";
    // 电台播放量
    public static final String NET_RADIO_PLAY_COUNT = "playCount";

    // MV 来源
    public static final String NET_MV_SOURCE = "source";
    // MV 类型
    public static final String NET_MV_TYPE = "type";
    // MV id
    public static final String NET_MV_ID = "id";
    // MV bvid
    public static final String NET_MV_BVID = "bvid";
    // MV 名
    public static final String NET_MV_NAME = "name";
    // MV 艺术家
    public static final String NET_MV_ARTIST = "artist";
    // MV 发布者 id
    public static final String NET_MV_CREATOR_ID = "creatorId";
    // MV 时长
    public static final String NET_MV_DURATION = "duration";
    // MV 发布时间
    public static final String NET_MV_PUB_TIME = "pubTime";
    // MV 封面图片 url
    public static final String NET_MV_COVER_IMG_URL = "coverImgUrl";
    // MV 播放量
    public static final String NET_MV_PLAY_COUNT = "playCount";

    // 榜单来源
    public static final String NET_RANKING_SOURCE = "source";
    // 榜单id
    public static final String NET_RANKING_ID = "id";
    // 榜单名称
    public static final String NET_RANKING_NAME = "name";
    // 榜单描述
    public static final String NET_RANKING_DESCRIPTION = "description";
    // 榜单播放量
    public static final String NET_RANKING_PLAY_COUNT = "playCount";
    // 榜单更新频率
    public static final String NET_RANKING_UPDATE_FRE = "updateFre";
    // 榜单更新时间
    public static final String NET_RANKING_UPDATE_TIME = "updateTime";
    // 榜单封面图片 url
    public static final String NET_RANKING_COVER_IMG_URL = "coverImgUrl";

    // 用户来源
    public static final String NET_USER_SOURCE = "source";
    // 用户 id
    public static final String NET_USER_ID = "id";
    // 用户名称
    public static final String NET_USER_NAME = "name";
    // 用户性别
    public static final String NET_USER_GENDER = "gender";
    // 用户头像 url
    public static final String NET_USER_AVATAR_URL = "avatarUrl";
    // 用户头像缩略图 url
    public static final String NET_USER_AVATAR_THUMB_URL = "avatarThumbUrl";
    // 用户关注数
    public static final String NET_USER_FOLLOW = "follow";
    // 用户粉丝数
    public static final String NET_USER_FAN = "fan";
    // 用户歌单数
    public static final String NET_USER_PLAYLIST_COUNT = "playlistCount";
    // 用户电台数
    public static final String NET_USER_RADIO_COUNT = "radioCount";
    // 用户节目数
    public static final String NET_USER_PROGRAM_COUNT = "programCount";

    // 在线音乐搜索历史
    public static final String NET_MUSIC_HISTORY_SEARCH = "historySearch";
    // 歌单搜索历史
    public static final String NET_PLAYLIST_HISTORY_SEARCH = "playlistHistorySearch";
    // 专辑搜索历史
    public static final String NET_ALBUM_HISTORY_SEARCH = "albumHistorySearch";
    // 歌手搜索历史
    public static final String NET_ARTIST_HISTORY_SEARCH = "artistHistorySearch";
    // 电台搜索历史
    public static final String NET_RADIO_HISTORY_SEARCH = "radioHistorySearch";
    // MV 搜索历史
    public static final String NET_MV_HISTORY_SEARCH = "mvHistorySearch";
    // 用户搜索历史
    public static final String NET_USER_HISTORY_SEARCH = "userHistorySearch";

    // 任务 url
    public static final String TASK_URL = "url";
    // 任务文件路径
    public static final String TASK_DEST = "dest";
    // 任务文件格式
    public static final String TASK_FORMAT = "format";
    // 任务类型
    public static final String TASK_TYPE = "type";
    // 任务的音乐信息
    public static final String TASK_MUSIC_INFO = "musicInfo";
    // 任务的 MV 信息
    public static final String TASK_MV_INFO = "mvInfo";
    // 任务名称
    public static final String TASK_NAME = "name";
    // 任务状态
    public static final String TASK_STATUS = "status";
    // 任务已完成大小
    public static final String TASK_FINISHED = "finished";
    // 任务总大小
    public static final String TASK_TOTAL = "total";
}
