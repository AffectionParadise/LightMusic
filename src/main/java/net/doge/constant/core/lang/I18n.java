package net.doge.constant.core.lang;

import net.doge.constant.core.config.ConfigConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Doge
 * @description 语言
 * @date 2020/12/7
 */
public class I18n {
    private static final Map<String, String[]> LANG_MAP = new HashMap<>();
    private static final int CHINESE_SIMPLIFIED = 0;
    private static final int ENGLISH = 1;
    public static int currLang = CHINESE_SIMPLIFIED;
    public static int solidLang;
    public static final String[] NAMES = {"简体中文", "English"};

    static {
        // 载入界面语言
        solidLang = currLang = ConfigConstants.CONFIG_DATA.getIntValue(ConfigConstants.UI_LANGUAGE, currLang);

        LANG_MAP.put("title", new String[]{"轻音", "LightMusic"});

//        LANG_MAP.put("songName", new String[]{"歌曲名：", "Title: "});
//        LANG_MAP.put("artist", new String[]{"艺术家：", "Artist: "});
//        LANG_MAP.put("albumName", new String[]{"专辑名：", "Album: "});

        LANG_MAP.put("paginationMsg", new String[]{"第 %s 页，共 %s 页", "Page %s of %s"});
        LANG_MAP.put("lyricLoadingMsg", new String[]{"加载歌词中......", "Loading lyric..."});
        LANG_MAP.put("noLyricMsg", new String[]{"尽情享受音乐的世界", "Enjoy the world of music to the fullest"});
        LANG_MAP.put("badFormatLyricMsg", new String[]{"该歌词不支持滚动", "The lyrics do not support scrolling"});
        LANG_MAP.put("loadingMsg", new String[]{"加载中，客官请稍等......", "Loading, please wait..."});
        LANG_MAP.put("loadFailed", new String[]{"数据加载失败", "Data load failed"});
        LANG_MAP.put("askDisposeMsg", new String[]{"你希望隐藏到托盘还是退出程序？", "Do you want to hide into the tray or exit the program?"});
        LANG_MAP.put("rememberChoiceMsg", new String[]{"记住我的选择", "Remember my choice"});
        LANG_MAP.put("exitOption1", new String[]{"隐藏到托盘", "Hide to tray"});
        LANG_MAP.put("exitOption2", new String[]{"退出程序", "Exit"});
        LANG_MAP.put("exitOption3", new String[]{"取消", "Cancel"});
        LANG_MAP.put("askRemoveLocalPlaylistMsg", new String[]{"是否删除当前收藏夹？此操作不可逆！", "Do you want to delete the current favorites? This action cannot be undone!"});
        LANG_MAP.put("askReimportMsg", new String[]{"将重新从所有歌曲目录导入歌曲，是否继续？", "Tracks will be re-imported from all track catalogs, do you want to continue?"});
        LANG_MAP.put("askRetainMusicListMsg", new String[]{"歌曲列表已存在歌曲，您希望保留歌曲列表的歌曲吗？(选择“否”将清空原有的歌曲列表)", "The track list already has tracks, do you want to keep the tracks from the track list? (Select \"No\" to clear the original track list)"});
        LANG_MAP.put("askClearCacheMsg", new String[]{"当前缓存大小为 %s，确定要清空缓存吗？", "The current cache size is %s, are you sure you want to clear the cache?"});
        LANG_MAP.put("askRemoveFileNotFoundMsg", new String[]{"该歌曲文件不存在，是否从列表中删除？", "The track file does not exist, remove it from the list?"});
        LANG_MAP.put("askRemoveItemsMsg", new String[]{"是否删除选中的项目？", "Do you want to delete the selected items?"});
        LANG_MAP.put("askClearListMsg", new String[]{"是否要清空列表？", "Do you want to clear the list?"});
        LANG_MAP.put("askDuplicateMsg", new String[]{"是否要删除列表中重复的项目？", "Do you want to remove duplicate items from the list?"});
        LANG_MAP.put("askReverseMsg", new String[]{"是否要倒置列表顺序？", "Do you want to invert the order of the list?"});
        LANG_MAP.put("updateCheckingMsg", new String[]{"检查更新中......", "Checking for updates..."});
        LANG_MAP.put("updateCheckFailedMsg", new String[]{"检查更新失败，请稍后再试", "Check update failed, please try again later"});
        LANG_MAP.put("updateMsg", new String[]{"\uD83D\uDE80 新版本已发布，是否更新？\n\n最新版本：%s\n当前版本：%s\n\n", "\uD83D\uDE80 A new version has been released, update now?\n\nLatest Version:%s\nCurrent Version:%s\n\n"});
        LANG_MAP.put("ignoreUpdateMsg", new String[]{"当有新版本时不再提示", "No more prompting when there is a new version"});
        LANG_MAP.put("updateReadyMsg", new String[]{"更新包已就绪，是否现在重启应用以完成更新？", "The update package is ready, do you want to restart the app now to complete the update?"});
        LANG_MAP.put("latestMsg", new String[]{"\uD83C\uDF89 当前已是最新版本\n\n最新版本：%s\n当前版本：%s\n\n", "\uD83C\uDF89 Currently the latest version\n\nLatest Version:%s\nCurrent Version:%s\n\n"});

        LANG_MAP.put("firstPageMsg", new String[]{"已经是第一页了", "Already the first page"});
        LANG_MAP.put("lastPageMsg", new String[]{"已经是最后一页了", "Already the last page"});
        LANG_MAP.put("illegalPageMsg", new String[]{"请输入合法页码", "Please enter a legal page number"});
        LANG_MAP.put("fileNotFoundMsg", new String[]{"文件不存在", "File does not exist"});
        LANG_MAP.put("unsupportedAudioFileMsg", new String[]{"不支持该格式的音频文件", "Audio files in this format are not supported"});
        LANG_MAP.put("invalidAudioFileMsg", new String[]{"不是有效的音频文件", "Not a valid audio file"});
        LANG_MAP.put("noCatalogMsg", new String[]{"无歌曲目录", "No track catalog"});
        LANG_MAP.put("noMusicMsg", new String[]{"没有可以播放的歌曲", "No tracks to play"});
        LANG_MAP.put("alreadyPlayingMsg", new String[]{"当前歌曲已经在播放", "The track selected is already playing"});
        LANG_MAP.put("noImgMsg", new String[]{"没有可加载的图片", "No images to load"});
        LANG_MAP.put("getResourceFailedMsg", new String[]{"获取资源失败", "Failed to get resources"});
        LANG_MAP.put("netErrorMsg", new String[]{"网络异常", "Network error"});
        LANG_MAP.put("timeOutMsg", new String[]{"请求超时", "Request timed out"});
        LANG_MAP.put("clearCacheSuccessMsg", new String[]{"清除缓存成功", "Cache cleared successfully"});
        LANG_MAP.put("nextPlaySuccessMsg", new String[]{"已添加到下一首", "Added to next track"});
        LANG_MAP.put("collectSuccessMsg", new String[]{"收藏成功", "Collected Successfully"});
        LANG_MAP.put("cancelCollectionSuccessMsg", new String[]{"取消收藏成功", "Collection canceled Successfully"});
        LANG_MAP.put("videoErrorMsg", new String[]{"播放视频时发生异常", "Video playback error"});
        LANG_MAP.put("removeSuccessMsg", new String[]{"删除成功", "Deleted successfully"});
        LANG_MAP.put("clearSuccessMsg", new String[]{"清空成功", "Cleared successfully"});
        LANG_MAP.put("duplicateSuccessMsg", new String[]{"去重成功", "Deduplicated successfully"});
        LANG_MAP.put("reverseSuccessMsg", new String[]{"倒序成功", "Reversed successfully"});
        LANG_MAP.put("changeDisabledMsg", new String[]{"已切换到播完暂停", "Switched to \"Pause after playback\""});
        LANG_MAP.put("changeSingleMsg", new String[]{"已切换到单曲循环", "Switched to \"Repeat one\""});
        LANG_MAP.put("changeListCycleMsg", new String[]{"已切换到列表循环", "Switched to \"Loop All\""});
        LANG_MAP.put("changeSequenceMsg", new String[]{"已切换到顺序播放", "Switched to \"Sequential playback\""});
        LANG_MAP.put("changeShuffleMsg", new String[]{"已切换到随机播放", "Switched to \"Shuffle\""});
        LANG_MAP.put("downloadCompletedMsg", new String[]{"下载完成", "Download Completed"});
        LANG_MAP.put("taskAddedMsg", new String[]{"已加入到下载队列", "Queued for download"});
        LANG_MAP.put("waitForTaskCompletedMsg", new String[]{"请等待下载任务完成", "Please wait for the download task to complete"});
        LANG_MAP.put("askRemoveSelectedTasksMsg", new String[]{"确定要删除选中任务吗？", "Are you sure you want to delete the selected task?"});
        LANG_MAP.put("askRemoveFileMsg", new String[]{"同时删除文件", "Delete files at the same time"});
        LANG_MAP.put("askRestartAllTasksMsg", new String[]{"是否要重新开始全部任务？", "Do you want to restart all the tasks?"});
        LANG_MAP.put("askCancelAllTasksMsg", new String[]{"是否要取消全部任务？", "Do you want to cancel all the tasks?"});
        LANG_MAP.put("askRemoveAllTasksMsg", new String[]{"确定要清空任务列表吗？", "Are you sure you want to clear your task list?"});
        LANG_MAP.put("askRemoveSongsFromPlayQueueMsg", new String[]{"是否从播放队列删除选中歌曲？", "Do you want to remove the selected tracks from the playback queue?"});
        LANG_MAP.put("askClearPlayQueueMsg", new String[]{"是否要清空播放队列？", "Do you want to clear the playback queue?"});
        LANG_MAP.put("loadingMvMsg", new String[]{"请稍候，MV 加载中......", "Please wait, the MV is loading..."});
        LANG_MAP.put("copySuccessMsg", new String[]{"复制成功", "Replicate Successfully"});

        LANG_MAP.put("playMenuItem", new String[]{"播放", "Play"});
        LANG_MAP.put("nextPlayMenuItem", new String[]{"下一首播放", "Next play"});
        LANG_MAP.put("openMenuItem", new String[]{"打开", "Open"});
        LANG_MAP.put("playAllMenuItem", new String[]{"播放全部", "Play all"});
        LANG_MAP.put("browseAlbumMenuItem", new String[]{"查看歌手专辑", "View albums"});
        LANG_MAP.put("browseMvMenuItem", new String[]{"查看歌手 MV", "View MV"});
        LANG_MAP.put("similarArtistMenuItem", new String[]{"查看相似歌手", "View similar artists"});
        LANG_MAP.put("artistFansMenuItem", new String[]{"查看歌手粉丝", "View followers"});
        LANG_MAP.put("artistBuddyMenuItem", new String[]{"查看歌手合作人", "View collaborators"});
        LANG_MAP.put("artistRadioMenuItem", new String[]{"查看歌手电台", "View radios"});
        LANG_MAP.put("artistPhotosMenuItem", new String[]{"查看歌手照片", "View photos"});
        LANG_MAP.put("userPlaylistMenuItem", new String[]{"查看用户歌单", "View playlists"});
        LANG_MAP.put("userAlbumMenuItem", new String[]{"查看用户专辑", "View albums"});
        LANG_MAP.put("userRadioMenuItem", new String[]{"查看用户电台", "View radios"});
        LANG_MAP.put("userVideoMenuItem", new String[]{"查看用户视频", "View videos"});
        LANG_MAP.put("userFollowMenuItem", new String[]{"查看用户关注", "View followings"});
        LANG_MAP.put("userFanMenuItem", new String[]{"查看用户粉丝", "View followers"});
        LANG_MAP.put("radioDjMenuItem", new String[]{"查看主播", "View streamer"});
        LANG_MAP.put("editInfoMenuItem", new String[]{"编辑歌曲信息", "Edit track info"});
        LANG_MAP.put("locateFileMenuItem", new String[]{"打开文件所在位置", "Locate file"});
        LANG_MAP.put("removeMenuItem", new String[]{"从列表删除", "Remove from list"});
        LANG_MAP.put("collectMenuItem", new String[]{"收藏", "Collect"});
        LANG_MAP.put("collectedMenuItem", new String[]{"已收藏", "Collected"});
        LANG_MAP.put("playMvMenuItem", new String[]{"播放 MV", "Play MV"});
        LANG_MAP.put("downloadMenuItem", new String[]{"下载", "Download"});
        LANG_MAP.put("commentMenuItem", new String[]{"查看评论", "View comments"});
        LANG_MAP.put("albumArtistMenuItem", new String[]{"查看歌手/作者", "View artist / author"});
        LANG_MAP.put("similarAlbumMenuItem", new String[]{"查看相似专辑", "View similar albums"});
        LANG_MAP.put("albumPhotosMenuItem", new String[]{"查看专辑照片", "View photos"});
        LANG_MAP.put("sheetMenuItem", new String[]{"查看乐谱", "View sheets"});
        LANG_MAP.put("searchSongMenuItem", new String[]{"搜索这首歌曲", "Search this track"});
        LANG_MAP.put("similarSongMenuItem", new String[]{"查看相似歌曲", "View similar tracks"});
        LANG_MAP.put("relatedPlaylistMenuItem", new String[]{"查看相关歌单", "View related playlists"});
        LANG_MAP.put("authorMenuItem", new String[]{"查看歌手/作者", "View artist / author"});
        LANG_MAP.put("albumMenuItem", new String[]{"查看专辑/电台", "View album / radio"});
        LANG_MAP.put("similarPlaylistMenuItem", new String[]{"查看相似歌单", "View similar playlists"});
        LANG_MAP.put("playlistCreatorMenuItem", new String[]{"查看创建者", "View creator"});
        LANG_MAP.put("playlistSubscriberMenuItem", new String[]{"查看收藏者", "View subscribers"});
        LANG_MAP.put("radioSubscriberMenuItem", new String[]{"查看订阅者", "View subscribers"});
        LANG_MAP.put("similarRadioMenuItem", new String[]{"查看相似电台", "View similar radios"});
        LANG_MAP.put("radioArtistsMenuItem", new String[]{"查看演职员/CV", "View cast & crew / CV"});
        LANG_MAP.put("radioPhotosMenuItem", new String[]{"查看电台照片", "View photos"});
        LANG_MAP.put("radioPostersMenuItem", new String[]{"查看电台海报", "View posters"});
        LANG_MAP.put("recRadioMenuItem", new String[]{"查看推荐电台", "View recommended radios"});
        LANG_MAP.put("relatedMvMenuItem", new String[]{"查看相关 MV", "View related MVs"});
        LANG_MAP.put("similarMvMenuItem", new String[]{"查看相似 MV", "View similar MVs"});
        LANG_MAP.put("videoEpisodeMenuItem", new String[]{"查看视频分集", "View episodes"});
        LANG_MAP.put("mvCreatorMenuItem", new String[]{"查看歌手/发布者", "View artist / publisher"});
        LANG_MAP.put("copyNameMenuItem", new String[]{"复制名称", "Copy name"});
        LANG_MAP.put("saveAlbumImage", new String[]{"导出专辑图片", "Export album image"});

        LANG_MAP.put("localMusic", new String[]{"本地音乐", "Local Music"});
        LANG_MAP.put("history", new String[]{"播放历史", "History"});
        LANG_MAP.put("collection", new String[]{"收藏", "Collection"});
        LANG_MAP.put("personalMusic", new String[]{"个人音乐", "Personal Music"});
        LANG_MAP.put("netMusic", new String[]{"音乐馆", "Tracks"});
        LANG_MAP.put("netPlaylist", new String[]{"歌单", "Playlists"});
        LANG_MAP.put("netAlbum", new String[]{"专辑", "Albums"});
        LANG_MAP.put("netArtist", new String[]{"歌手", "Artists"});
        LANG_MAP.put("netRadio", new String[]{"电台", "Radios"});
        LANG_MAP.put("netMv", new String[]{"MV", "MVs"});
        LANG_MAP.put("netRank", new String[]{"榜单", "Ranks"});
        LANG_MAP.put("netUser", new String[]{"用户", "Users"});
        LANG_MAP.put("netRecommend", new String[]{"推荐", "Recommendations"});
        LANG_MAP.put("downloadManagement", new String[]{"下载管理", "Download Management"});
        LANG_MAP.put("playQueue", new String[]{"播放队列", "Playback Queue"});

        LANG_MAP.put("musicCollection", new String[]{"歌曲", "Tracks"});
        LANG_MAP.put("playlistCollection", new String[]{"歌单", "Playlists"});
        LANG_MAP.put("albumCollection", new String[]{"专辑", "Albums"});
        LANG_MAP.put("artistCollection", new String[]{"歌手", "Artists"});
        LANG_MAP.put("radioCollection", new String[]{"电台", "Radios"});
        LANG_MAP.put("mvCollection", new String[]{" MV ", "MVs"});
        LANG_MAP.put("rankCollection", new String[]{"榜单", "Ranks"});
        LANG_MAP.put("userCollection", new String[]{"用户", "Users"});

        LANG_MAP.put("miniWindowTip", new String[]{"迷你模式", "Mini mode"});
        LANG_MAP.put("minimizeWindowTip", new String[]{"最小化", "Minimize"});
        LANG_MAP.put("maximizeWindowTip", new String[]{"最大化", "Maximize"});
        LANG_MAP.put("closeWindowTip", new String[]{"关闭", "Close"});
        LANG_MAP.put("changeToLyricPaneTip", new String[]{"切换到歌曲详情页", "Switch to track detail page"});
        LANG_MAP.put("changeToMusicPaneTip", new String[]{"切换到列表页", "Switch to track list page"});
        LANG_MAP.put("collectTip", new String[]{"收藏", "Collect"});
        LANG_MAP.put("collectedTip", new String[]{"已收藏", "Collected"});
        LANG_MAP.put("downloadTip", new String[]{"下载", "Download"});
        LANG_MAP.put("commentTip", new String[]{"评论", "Comment"});
        LANG_MAP.put("mvTip", new String[]{"播放 MV", "Play MV"});
        LANG_MAP.put("playTip", new String[]{"播放", "Play"});
        LANG_MAP.put("pauseTip", new String[]{"暂停", "Pause"});
        LANG_MAP.put("lastTip", new String[]{"上一首", "Previous"});
        LANG_MAP.put("nextTip", new String[]{"下一首", "Next"});
        LANG_MAP.put("backwTip", new String[]{"快退", "Rewind"});
        LANG_MAP.put("forwTip", new String[]{"快进", "Fast forward"});
        LANG_MAP.put("playModeDisabledTip", new String[]{"播完暂停", "Pause after playback"});
        LANG_MAP.put("singleTip", new String[]{"单曲循环", "Repeat one"});
        LANG_MAP.put("sequenceTip", new String[]{"顺序播放", "Sequential playback"});
        LANG_MAP.put("listCycleTip", new String[]{"列表循环", "Loop all"});
        LANG_MAP.put("shuffleTip", new String[]{"随机播放", "Shuffle"});
        LANG_MAP.put("soundTip", new String[]{"声音开启", "Sound on"});
        LANG_MAP.put("muteTip", new String[]{"静音", "Mute"});
        LANG_MAP.put("rateTip", new String[]{"倍速", "Rate"});
        LANG_MAP.put("fobTimeTip", new String[]{"快进/快退时间", "Fast forward/Rewind time"});
        LANG_MAP.put("fullScreenTip", new String[]{"全屏", "Full screen"});
        LANG_MAP.put("switchSpectrumTip", new String[]{"频谱", "Spectrum"});
        LANG_MAP.put("switchBlurTip", new String[]{"模糊", "Blur"});
        LANG_MAP.put("soundEffectTip", new String[]{"音效", "Sound effect"});
        LANG_MAP.put("sheetTip", new String[]{"乐谱", "Sheet"});
        LANG_MAP.put("originalLyricTip", new String[]{"原歌词", "Original lyric"});
        LANG_MAP.put("translationTip", new String[]{"翻译", "Translation"});
        LANG_MAP.put("romaTip", new String[]{"罗马音", "Roma"});
        LANG_MAP.put("tradChineseTip", new String[]{"繁体", "Traditional Chinese"});
        LANG_MAP.put("menuTip", new String[]{"主菜单", "Main menu"});
        LANG_MAP.put("goToPlayQueueTip", new String[]{"转到播放队列", "Go to playback queue"});
        LANG_MAP.put("desktopLyricTip", new String[]{"桌面歌词", "Desktop lyric"});
        LANG_MAP.put("localPlaylistTip", new String[]{"收藏夹管理", "Manage favorites"});
        LANG_MAP.put("addTip", new String[]{"添加歌曲文件", "Add tracks"});
        LANG_MAP.put("reimportTip", new String[]{"重新从歌曲目录导入歌曲", "Re-import tracks"});
        LANG_MAP.put("manageCatalogTip", new String[]{"管理歌曲目录", "Manage track catalogs"});
        LANG_MAP.put("removeTip", new String[]{"删除歌曲", "Remove tracks"});
        LANG_MAP.put("clearTip", new String[]{"清空列表", "Clear list"});
        LANG_MAP.put("duplicateTip", new String[]{"去重", "Deduplicate"});
        LANG_MAP.put("reverseTip", new String[]{"倒序", "Reverse"});
        LANG_MAP.put("sortTip", new String[]{"排序", "Sort"});
        LANG_MAP.put("moveUpTip", new String[]{"上移", "Move up"});
        LANG_MAP.put("moveDownTip", new String[]{"下移", "Move down"});
        LANG_MAP.put("clearInputTip", new String[]{"清除输入", "Clear entry"});
        LANG_MAP.put("styleTip", new String[]{"换肤", "Theme"});
        LANG_MAP.put("hideDetailTip", new String[]{"隐藏歌曲详情页", "Hide details"});
        LANG_MAP.put("showKeywordPanelTip", new String[]{"显示关键词面板", "Show keyword panel"});
        LANG_MAP.put("searchTip", new String[]{"搜索", "Search"});
        LANG_MAP.put("backwardTip", new String[]{"后退", "Back"});
        LANG_MAP.put("playAllTip", new String[]{"播放全部", "Play all"});
        LANG_MAP.put("refreshTip", new String[]{"刷新", "Refresh"});
        LANG_MAP.put("startPageTip", new String[]{"第一页", "Page one"});
        LANG_MAP.put("lastPageTip", new String[]{"上一页", "Previous"});
        LANG_MAP.put("goTip", new String[]{"跳页", "Go to"});
        LANG_MAP.put("nextPageTip", new String[]{"下一页", "Next"});
        LANG_MAP.put("endPageTip", new String[]{"最后一页", "The last page"});
        LANG_MAP.put("restartSelectedTasksTip", new String[]{"重新开始选中任务", "Restart selected tasks"});
        LANG_MAP.put("cancelSelectedTasksTip", new String[]{"取消选中任务", "Cancel selected tasks"});
        LANG_MAP.put("removeSelectedTasksTip", new String[]{"删除选中任务", "Remove selected tasks"});
        LANG_MAP.put("restartAllTasksTip", new String[]{"重新开始全部任务", "Restart all tasks"});
        LANG_MAP.put("cancelAllTasksTip", new String[]{"取消全部任务", "Cancel all tasks"});
        LANG_MAP.put("removeAllTasksTip", new String[]{"删除全部任务", "Remove all tasks"});
        LANG_MAP.put("removeFromPlayQueueTip", new String[]{"删除选中歌曲", "Remove selected tracks"});
        LANG_MAP.put("clearPlayQueueTip", new String[]{"清空播放队列", "Clear playback queue"});
        LANG_MAP.put("clearHistorySearchTip", new String[]{"清空搜索历史", "Clear search histories"});
        LANG_MAP.put("removeHistoryKeywordTip", new String[]{"右击删除该历史", "Click right to remove it"});

        LANG_MAP.put("setting", new String[]{"设置", "Settings"});
        LANG_MAP.put("closeSong", new String[]{"关闭当前歌曲", "Close current track"});
        LANG_MAP.put("clearCache", new String[]{"清空播放缓存", "Clear cache"});
        LANG_MAP.put("manageStyle", new String[]{"更换主题", "Themes"});
        LANG_MAP.put("styleCustom", new String[]{"添加自定义主题", "Customize theme"});
        LANG_MAP.put("donate", new String[]{"捐赠 & 感谢", "Donation & Thanks"});
        LANG_MAP.put("release", new String[]{"发布页", "Release"});
        LANG_MAP.put("update", new String[]{"检查更新", "Check for updates"});
        LANG_MAP.put("help", new String[]{"指南", "Helps"});
        LANG_MAP.put("about", new String[]{"关于", "Abouts"});

        LANG_MAP.put("copySongName", new String[]{"复制歌曲名", "Copy track name"});
        LANG_MAP.put("copyArtist", new String[]{"复制艺术家", "Copy artist"});
        LANG_MAP.put("copyAlbum", new String[]{"复制专辑名", "Copy album"});

        LANG_MAP.put("specGradientMsg", new String[]{"频谱透明渐变", "Spectrum transparent gradient"});
        LANG_MAP.put("specOpacityMsg", new String[]{"当前频谱透明度：%d%%", "Current spectrum view opacity: %d%%"});

        LANG_MAP.put("copyLyric", new String[]{"复制这句歌词", "Copy lyric"});
        LANG_MAP.put("locateLyric", new String[]{"定位歌词时间", "Seek to lyric time"});
        LANG_MAP.put("browseLyric", new String[]{"查看歌词文件", "View lyric file"});
        LANG_MAP.put("downloadLyric", new String[]{"下载歌词文件", "Download lyric file"});

        LANG_MAP.put("lyricOffsetMsg", new String[]{"当前歌词偏移：%.1f s", "Current lyric offset: %.1f s"});
        LANG_MAP.put("reset", new String[]{"重置", "Reset"});

        LANG_MAP.put("playModeDisabled", new String[]{"播完暂停", "Pause after playback"});
        LANG_MAP.put("single", new String[]{"单曲循环", "Repeat one"});
        LANG_MAP.put("sequence", new String[]{"顺序播放", "Sequential playback"});
        LANG_MAP.put("listCycle", new String[]{"列表循环", "Loop all"});
        LANG_MAP.put("shuffle", new String[]{"随机播放", "Shuffle"});

        LANG_MAP.put("originalLyric", new String[]{"原歌词", "Original lyric"});
        LANG_MAP.put("translation", new String[]{"翻译", "Translation"});
        LANG_MAP.put("roma", new String[]{"罗马音", "Roma"});
        LANG_MAP.put("tradChinese", new String[]{"繁体", "Traditional Chinese"});

        LANG_MAP.put("fluid", new String[]{"旋转流体", "Fluid"});
        LANG_MAP.put("gs", new String[]{"高斯模糊", "Gaussian blur"});
        LANG_MAP.put("darker", new String[]{"暗角滤镜", "Darker mode"});
        LANG_MAP.put("mask", new String[]{"朦胧遮罩", "Mask"});
        LANG_MAP.put("groove", new String[]{"旋转律动", "Groove"});
        LANG_MAP.put("blurOff", new String[]{"跟随主题", "Follow theme"});
        LANG_MAP.put("cvBlur", new String[]{"歌曲封面", "Track cover"});
        LANG_MAP.put("mcBlur", new String[]{"纯主色调", "Solid color"});
        LANG_MAP.put("lgBlur", new String[]{"线性渐变", "Linear gradient"});
        LANG_MAP.put("fbmBlur", new String[]{"迷幻纹理", "Psychedelic texture"});

        LANG_MAP.put("emptyHint", new String[]{"列表空空如也~", "Empty list"});

        LANG_MAP.put("playAll", new String[]{"播放全部", "Play all"});

        LANG_MAP.put("createLocalPlaylist", new String[]{"新建收藏夹", "Create a new favorites"});
        LANG_MAP.put("localPlaylistName", new String[]{"收藏夹名称：", "Favorites name:"});
        LANG_MAP.put("editLocalPlaylist", new String[]{"编辑收藏夹", "Edit this favorites"});
        LANG_MAP.put("removeLocalPlaylist", new String[]{"删除收藏夹", "Remove this favorites"});
        LANG_MAP.put("localPlaylistNameNotNullMsg", new String[]{"收藏夹名称不能为空", "Favorites name can not be empty"});

        LANG_MAP.put("addFile", new String[]{"添加歌曲文件", "Add tracks"});
        LANG_MAP.put("addDir", new String[]{"添加歌曲文件夹", "Add track folders"});

        LANG_MAP.put("ascending", new String[]{"升序", "Ascend"});
        LANG_MAP.put("descending", new String[]{"降序", "Descend"});
        LANG_MAP.put("sortBySongNameAndFileName", new String[]{"按曲名/文件名混合", "By track name & file name"});
        LANG_MAP.put("sortBySongName", new String[]{"按曲名", "By track name"});
        LANG_MAP.put("sortByArtist", new String[]{"按艺术家", "By artist"});
        LANG_MAP.put("sortByAlbumName", new String[]{"按专辑", "By album"});
        LANG_MAP.put("sortByFileName", new String[]{"按文件名", "By file name"});
        LANG_MAP.put("sortByTime", new String[]{"按时长", "By duration"});
        LANG_MAP.put("sortByCreationTime", new String[]{"按创建时间", "By creation time"});
        LANG_MAP.put("sortByLastModifiedTime", new String[]{"按修改时间", "By modified time"});
        LANG_MAP.put("sortByLastAccessTime", new String[]{"按访问时间", "By access time"});
        LANG_MAP.put("sortBySize", new String[]{"按大小", "By file size"});

        // 表头
        LANG_MAP.put("sourceHeader", new String[]{"源", "Source"});
        LANG_MAP.put("nameHeader", new String[]{"标题", "Title"});
        LANG_MAP.put("artistHeader", new String[]{"艺术家", "Artist"});
        LANG_MAP.put("albumHeader", new String[]{"专辑", "Album"});
        LANG_MAP.put("durationHeader", new String[]{"时长", "Duration"});
        LANG_MAP.put("typeHeader", new String[]{"类型", "Type"});
        LANG_MAP.put("sizeHeader", new String[]{"大小", "Size"});
        LANG_MAP.put("progressHeader", new String[]{"进度", "Progress"});
        LANG_MAP.put("percentHeader", new String[]{"百分比", "Percent"});
        LANG_MAP.put("statusHeader", new String[]{"状态", "Status"});

        LANG_MAP.put("searchSuggestion", new String[]{"搜索建议", "Suggestion"});
        LANG_MAP.put("hotSearch", new String[]{"热门搜索", "Hot"});
        LANG_MAP.put("historySearch", new String[]{"搜索历史", "History"});

        LANG_MAP.put("playlistId", new String[]{"歌单 ID", "Playlist ID"});

        LANG_MAP.put("playlistRecommend", new String[]{"推荐歌单", "Rcmd playlists"});
        LANG_MAP.put("highQualityPlaylist", new String[]{"精品歌单", "HQ playlists"});
        LANG_MAP.put("hotMusic", new String[]{"飙升歌曲", "Hot tracks"});
        LANG_MAP.put("newMusic", new String[]{"新歌速递", "New tracks"});
        LANG_MAP.put("newAlbum", new String[]{"新碟上架", "New albums"});
        LANG_MAP.put("artistList", new String[]{"歌手排行", "Artist rank"});
        LANG_MAP.put("newRadio", new String[]{"新晋电台", "New radios"});
        LANG_MAP.put("hotRadio", new String[]{"热门电台", "Hot radios"});
        LANG_MAP.put("programRecommend", new String[]{"推荐节目", "Rcmd programs"});
        LANG_MAP.put("mvRecommend", new String[]{"推荐 MV", "Rcmd MVs"});

        LANG_MAP.put("cancelTaskMenuItem", new String[]{"取消下载", "Cancel"});
        LANG_MAP.put("restartTaskMenuItem", new String[]{"重新下载", "Restart"});
        LANG_MAP.put("removeTaskMenuItem", new String[]{"删除任务", "Remove"});

        LANG_MAP.put("totalMsg", new String[]{"共 %s 项", "%s items"});

        LANG_MAP.put("saveDescCover", new String[]{"导出封面", "Save cover"});
        LANG_MAP.put("saveDescBg", new String[]{"导出背景", "Save background image"});
        LANG_MAP.put("copyDescName", new String[]{"复制名称", "Copy name"});
        LANG_MAP.put("copyDescTag", new String[]{"复制标签", "Copy tag"});
        LANG_MAP.put("copyDesc", new String[]{"复制描述", "Copy description"});

        LANG_MAP.put("commentCopy", new String[]{"复制评论", "Copy comment"});
        LANG_MAP.put("saveProfile", new String[]{"导出用户头像", "Save avatar"});
        LANG_MAP.put("commentUser", new String[]{"查看用户", "View user"});
        LANG_MAP.put("commentPlaylist", new String[]{"查看用户歌单", "View user playlist"});
        LANG_MAP.put("commentAlbum", new String[]{"查看用户专辑", "View user album"});

        LANG_MAP.put("sheetBrowse", new String[]{"查看乐谱", "View sheet"});
        LANG_MAP.put("sheetCopy", new String[]{"复制名称", "Copy name"});

        LANG_MAP.put("copyMotto", new String[]{"复制格言", "Copy"});
        LANG_MAP.put("nextMotto", new String[]{"下一条格言", "Next Note"});

        LANG_MAP.put("openMainFrame", new String[]{"打开主界面", "Show main frame"});
        LANG_MAP.put("exit", new String[]{"退出", "Exit"});

        LANG_MAP.put("updateInfoMsg", new String[]{"更新说明：\n", "Details：\n"});
        LANG_MAP.put("ok", new String[]{"确定", "OK"});
        LANG_MAP.put("yes", new String[]{"是", "Yes"});
        LANG_MAP.put("no", new String[]{"否", "No"});
        LANG_MAP.put("cancel", new String[]{"取消", "Cancel"});
        LANG_MAP.put("toRelease", new String[]{"前往发布页", "Go to release page"});
        LANG_MAP.put("restartNow", new String[]{"立即重启", "Restart now"});
        LANG_MAP.put("later", new String[]{"以后再说", "Later"});

        LANG_MAP.put("chooseTrackFile", new String[]{"选择歌曲文件", "Choose track files"});
        LANG_MAP.put("chooseTrackFolder", new String[]{"选择歌曲文件夹", "Choose track folder"});
        LANG_MAP.put("saveImg", new String[]{"保存图片", "Save image"});
        LANG_MAP.put("audioFile", new String[]{"音频文件", "Audio files"});
        LANG_MAP.put("trackAdded", new String[]{"成功添加 %s 首歌曲", "%s Tracks Added"});

        LANG_MAP.put("recentWeek", new String[]{"最近一周", "Recent week"});
        LANG_MAP.put("allTime", new String[]{"所有时间", "All time"});
        LANG_MAP.put("latest", new String[]{"最新发布", "Latest"});
        LANG_MAP.put("mostPlayed", new String[]{"最多播放", "Most played"});
        LANG_MAP.put("mostCollected", new String[]{"最多收藏", "Most collected"});
        LANG_MAP.put("ascend", new String[]{"正序", "Ascend"});
        LANG_MAP.put("descend", new String[]{"倒序", "Descend"});

        LANG_MAP.put("filterByKeyword", new String[]{"关键字筛选", "Filter by keyword"});

        LANG_MAP.put("general", new String[]{"常规", "General"});
        LANG_MAP.put("lyric", new String[]{"歌词", "Lyric"});
        LANG_MAP.put("program", new String[]{"节目", "Program"});

        LANG_MAP.put("hotComment", new String[]{"热门", "Hot"});
        LANG_MAP.put("newComment", new String[]{"最新", "Latest"});

        LANG_MAP.put("ready", new String[]{"已就绪：", "Ready: "});
        LANG_MAP.put("playing", new String[]{"播放中：", "Playing: "});
        LANG_MAP.put("pausing", new String[]{"暂停中：", "Pausing: "});
        LANG_MAP.put("stopped", new String[]{"已停止：", "Stopped: "});
        LANG_MAP.put("loadTrack", new String[]{"加载中：", "Loading: "});
        LANG_MAP.put("loadTrackFailed", new String[]{"加载失败：", "Load Failed: "});
        LANG_MAP.put("refreshUrl", new String[]{"刷新 URL 中：", "Refreshing URL: "});

        LANG_MAP.put("userCommented", new String[]{"发起评论的用户", "User who commented"});
        LANG_MAP.put("commentSuffix", new String[]{" 的评论", "'s comment"});
        LANG_MAP.put("similarTrackSuffix", new String[]{" 的相似歌曲", "'s similar tracks"});
        LANG_MAP.put("playlistSuffix", new String[]{" 的歌单", "'s playlists"});
        LANG_MAP.put("relatedPlaylistSuffix", new String[]{" 的相关歌单", "'s related playlists"});
        LANG_MAP.put("similarPlaylistSuffix", new String[]{" 的相似歌单", "'s similar playlists"});
        LANG_MAP.put("authorSuffix", new String[]{" 的作者", "'s author"});
        LANG_MAP.put("artistSuffix", new String[]{" 的歌手", "'s artist"});
        LANG_MAP.put("similarArtistSuffix", new String[]{" 的相似歌手", "'s similar artists"});
        LANG_MAP.put("albumSuffix", new String[]{" 的专辑", "'s albums"});
        LANG_MAP.put("similarAlbumSuffix", new String[]{" 的相似专辑", "'s similar albums"});
        LANG_MAP.put("radioSuffix", new String[]{" 的电台", "'s radios"});
        LANG_MAP.put("similarRadioSuffix", new String[]{" 的相似电台", "'s similar radios"});
        LANG_MAP.put("recRadioSuffix", new String[]{" 的推荐电台", "'s rcmd radios"});
        LANG_MAP.put("mvSuffix", new String[]{" 的 MV", "'s MVs"});
        LANG_MAP.put("similarMvSuffix", new String[]{" 的相似 MV", "'s similar MVs"});
        LANG_MAP.put("relatedMvSuffix", new String[]{" 的相关 MV", "'s related MVs"});
        LANG_MAP.put("creatorSuffix", new String[]{" 的创建者", "'s creator"});
        LANG_MAP.put("collectorSuffix", new String[]{" 的收藏者", "'s collector"});
        LANG_MAP.put("followSuffix", new String[]{" 的关注", "'s follows"});
        LANG_MAP.put("fanSuffix", new String[]{" 的粉丝", "'s followers"});
        LANG_MAP.put("buddySuffix", new String[]{" 的合作人", "'s collaborators"});
        LANG_MAP.put("djSuffix", new String[]{" 的主播", "'s streamer"});
        LANG_MAP.put("subscriberSuffix", new String[]{" 的订阅者", "'s subscribers"});
        LANG_MAP.put("castSuffix", new String[]{" 的演职员", "'s casts"});
        LANG_MAP.put("episodeSuffix", new String[]{" 的分集", "'s episodes"});
        LANG_MAP.put("publisherSuffix", new String[]{" 的发布者", "'s publisher"});
        LANG_MAP.put("videoSuffix", new String[]{" 的视频", "'s videos"});
        LANG_MAP.put("sheetSuffix", new String[]{" 的乐谱", "'s sheets"});

        LANG_MAP.put("aboutTitle", new String[]{"关于", "Abouts"});
        LANG_MAP.put("version", new String[]{"版本：", "Version: "});
        LANG_MAP.put("jdkVersion", new String[]{"JDK 版本：", "JDK Version: "});
        LANG_MAP.put("techno", new String[]{"基于 Swing 与 JavaFX 构建", "Built on Swing and JavaFX"});
        LANG_MAP.put("website", new String[]{"网址：", "Website: "});
        LANG_MAP.put("mail", new String[]{"邮箱：", "Mail: "});

        LANG_MAP.put("presets", new String[]{"预设", "Presets"});
        LANG_MAP.put("preset", new String[]{"预设", "Preset"});
        LANG_MAP.put("custom", new String[]{"自定义", "Custom"});
        LANG_MAP.put("inUse", new String[]{"使用中", "Using"});
        LANG_MAP.put("chooseColor", new String[]{"选择颜色", "Choose color"});

        LANG_MAP.put("customStyleTitle", new String[]{"自定义主题", "Customize theme"});
        LANG_MAP.put("styleNameNotNullMsg", new String[]{"emmm~~主题名称不能为无名氏哦", "Theme name can not be empty"});
        LANG_MAP.put("styleNameDuplicateMsg", new String[]{"emmm~该主题名称已存在，换一个吧", "Theme name already exists, change one please"});
        LANG_MAP.put("imgFileNotExistMsg", new String[]{"选定的图片路径无效", "Invalid image path"});
        LANG_MAP.put("imgNotValidMsg", new String[]{"不是有效的图片文件", "Not a valid image file"});
        LANG_MAP.put("styleName", new String[]{"主题名称：", "Theme name: "});
        LANG_MAP.put("bgImg", new String[]{"背景图片：", "Background image: "});
        LANG_MAP.put("foreColor", new String[]{"列表悬停框颜色：", "List hover color: "});
        LANG_MAP.put("selectedColor", new String[]{"列表选中框颜色：", "List selected color: "});
        LANG_MAP.put("lyricTextColor", new String[]{"歌词文字颜色：", "Lyric text color: "});
        LANG_MAP.put("lyricHighlightColor", new String[]{"歌词高亮颜色：", "Lyric highlight color: "});
        LANG_MAP.put("uiTextColor", new String[]{"界面文字颜色：", "UI text color: "});
        LANG_MAP.put("timeBarColor", new String[]{"时间条颜色：", "Progress bar color: "});
        LANG_MAP.put("iconColor", new String[]{"图标颜色：", "Icon color: "});
        LANG_MAP.put("scrollBarColor", new String[]{"滚动条颜色：", "Scroll bar color: "});
        LANG_MAP.put("volumeBarColor", new String[]{"音量条颜色：", "Volume bar color: "});
        LANG_MAP.put("spectrumColor", new String[]{"频谱颜色：", "Spectrum color: "});
        LANG_MAP.put("browseImg", new String[]{"选择图片", "Browse"});
        LANG_MAP.put("chooseImg", new String[]{"选择图片", "Choose Image"});
        LANG_MAP.put("imgFile", new String[]{"图片文件", "Image files"});
        LANG_MAP.put("solidColor", new String[]{"纯色", "Solid color"});
        LANG_MAP.put("addStyle", new String[]{"添加", "Add"});
        LANG_MAP.put("addAndApplyStyle", new String[]{"添加并应用", "Apply"});
        LANG_MAP.put("updateStyle", new String[]{"更新", "Update"});

        LANG_MAP.put("desktopLyricTitle", new String[]{"桌面歌词", "Desktop lyric"});
        LANG_MAP.put("lockTip", new String[]{"锁定", "Lock"});
        LANG_MAP.put("unlockTip", new String[]{"解锁", "Unlock"});
        LANG_MAP.put("restoreTip", new String[]{"还原位置", "Restore location"});
        LANG_MAP.put("descendTransTip", new String[]{"减少不透明度", "Decrease text opacity"});
        LANG_MAP.put("ascendTransTip", new String[]{"增加不透明度", "Increase text opacity"});
        LANG_MAP.put("decreaseFontTip", new String[]{"缩小字体", "Decrease font size"});
        LANG_MAP.put("increaseFontTip", new String[]{"放大字体", "Increase font size"});
        LANG_MAP.put("onTopTip", new String[]{"置顶", "On top"});
        LANG_MAP.put("cancelOnTopTip", new String[]{"取消置顶", "Unpin from top"});
        LANG_MAP.put("closeTip", new String[]{"关闭", "Close"});

        LANG_MAP.put("donateTitle", new String[]{"捐赠 & 感谢", "Donation & Thanks"});
        LANG_MAP.put("thankMsg", new String[]{"如果您觉得这款软件还不错，可以请作者喝杯咖啡~~", "If you think this software is not bad, you can invite the author to have a cup of coffee :)"});
        LANG_MAP.put("weixin", new String[]{"微信", "Wechat"});
        LANG_MAP.put("alipay", new String[]{"支付宝", "Alipay"});

        LANG_MAP.put("editInfoTitle", new String[]{"歌曲信息", "Track file info"});
        LANG_MAP.put("fileUsedMsg", new String[]{"文件正在被占用，无法修改", "File occupied"});
        LANG_MAP.put("fileName", new String[]{"文件名：", "File name: "});
        LANG_MAP.put("filePath", new String[]{"文件路径：", "File path: "});
        LANG_MAP.put("fileSize", new String[]{"文件大小：", "File size: "});
        LANG_MAP.put("creationTime", new String[]{"创建时间：", "Creation time: "});
        LANG_MAP.put("modificationTime", new String[]{"修改时间：", "Modification time: "});
        LANG_MAP.put("accessTime", new String[]{"访问时间：", "Access time: "});
        LANG_MAP.put("fileDuration", new String[]{"时长：", "Duration: "});
        LANG_MAP.put("fileTitle", new String[]{"标题：", "Title: "});
        LANG_MAP.put("fileArtist", new String[]{"艺术家：", "Artist: "});
        LANG_MAP.put("fileAlbum", new String[]{"专辑：", "Album: "});
        LANG_MAP.put("fileGenre", new String[]{"流派：", "Genre: "});
        LANG_MAP.put("fileLyrics", new String[]{"歌词：", "Lyrics: "});
        LANG_MAP.put("fileLyricist", new String[]{"作词：", "Lyricist: "});
        LANG_MAP.put("fileYear", new String[]{"年份：", "Year: "});
        LANG_MAP.put("fileRating", new String[]{"评分：", "Rating: "});
        LANG_MAP.put("fileBpm", new String[]{"BPM：", "BPM: "});
        LANG_MAP.put("fileKey", new String[]{"调性：", "Key: "});
        LANG_MAP.put("fileComment", new String[]{"注释：", "Comment: "});
        LANG_MAP.put("fileRecordLabel", new String[]{"厂牌：", "Record label: "});
        LANG_MAP.put("fileMood", new String[]{"情绪：", "Mood: "});
        LANG_MAP.put("fileOccasion", new String[]{"场合：", "Occasion: "});
        LANG_MAP.put("fileLanguage", new String[]{"语言：", "Language: "});
        LANG_MAP.put("fileCountry", new String[]{"地区：", "Country: "});
        LANG_MAP.put("fileVersion", new String[]{"版本：", "Version: "});
        LANG_MAP.put("fileCopyright", new String[]{"版权：", "Copyright: "});
        LANG_MAP.put("coverImg", new String[]{"封面图片：", "Cover image: "});
        LANG_MAP.put("save", new String[]{"保存", "Save"});

        LANG_MAP.put("imageViewTitle", new String[]{"图片预览", "Image view"});
        LANG_MAP.put("loadingImage", new String[]{"请稍候，图片加载中......", "Loading image, please wait..."});
        LANG_MAP.put("imgFirstPage", new String[]{"已经是第一张了", "Already the first image"});
        LANG_MAP.put("imgLastPage", new String[]{"已经是最后一张了", "Already the last image"});
        LANG_MAP.put("imgIllegalPage", new String[]{"请输入合法页码", "Please entry legal page"});
        LANG_MAP.put("adapt", new String[]{"缩放以适应", "Adapt"});
        LANG_MAP.put("zoomIn", new String[]{"放大", "Zoom in"});
        LANG_MAP.put("zoomOut", new String[]{"缩小", "Zoom out"});
        LANG_MAP.put("leftRotate", new String[]{"逆时针旋转 90 度", "Rotate counterclockwise 90 degrees"});
        LANG_MAP.put("rightRotate", new String[]{"顺时针旋转 90 度", "Rotate clockwise 90 degrees"});
        LANG_MAP.put("lastImg", new String[]{"上一张", "Previous"});
        LANG_MAP.put("nextImg", new String[]{"下一张", "Next"});
        LANG_MAP.put("firstImg", new String[]{"第一张", "First"});
        LANG_MAP.put("lstImg", new String[]{"最后一张", "Last"});
        LANG_MAP.put("imgLost", new String[]{"图片走丢了T_T", "Image lost :("});

        LANG_MAP.put("addToFavoritesTitle", new String[]{"收藏歌曲", "Add to favorites"});
        LANG_MAP.put("addToFavoritesTip", new String[]{"将 %s 添加到...", "Add %s to favorites"});
        LANG_MAP.put("addMultiToFavoritesTip", new String[]{"将 %s 等 %s 首歌曲添加到...", "Add %s among %s songs to favorites"});

        LANG_MAP.put("manageCatalogTitle", new String[]{"管理歌曲目录", "Track catalogs"});
        LANG_MAP.put("askRemoveCatalogMsg", new String[]{"确定删除选中的目录？", "Sure to delete the selected catalogs?"});
        LANG_MAP.put("catalogExistsMsg", new String[]{"目录已存在，无法重复添加", "Catalog already added"});
        LANG_MAP.put("catalogNotFoundMsg", new String[]{"该目录不存在", "Catalog not found"});
        LANG_MAP.put("catalogTip", new String[]{"重新导入歌曲时将从以下目录查找歌曲", "Tracks will be imported from the following directories"});
        LANG_MAP.put("dialogAll", new String[]{"全选", "All"});
        LANG_MAP.put("dialogInvert", new String[]{"反选", "Invert"});
        LANG_MAP.put("dialogOpen", new String[]{"打开", "Open"});
        LANG_MAP.put("dialogApply", new String[]{"应用", "Apply"});
        LANG_MAP.put("dialogNew", new String[]{"新建", "New"});
        LANG_MAP.put("dialogAdd", new String[]{"添加", "Add"});
        LANG_MAP.put("dialogEdit", new String[]{"编辑", "Edit"});
        LANG_MAP.put("dialogRemove", new String[]{"删除", "Remove"});

        LANG_MAP.put("manageStyleTitle", new String[]{"管理主题", "Themes"});
        LANG_MAP.put("bgImgLost", new String[]{"主题背景图片丢失，请重新编辑主题", "Background image is missing, please check again"});
        LANG_MAP.put("editDenied", new String[]{"不能编辑预设的主题", "Editing a preset theme is not permitted"});
        LANG_MAP.put("removeDenied", new String[]{"不能删除预设的主题", "Removing a preset theme is not permitted"});
        LANG_MAP.put("askRemoveStyleMsg", new String[]{"确定删除选中的主题？", "Sure to remove selected themes?"});
        LANG_MAP.put("singleSelectionMsg", new String[]{"需要编辑的主题一次只能选择一个", "Only single selection for editing permitted"});
        LANG_MAP.put("manageStyleTip", new String[]{"应用、添加、编辑或删除主题（预设主题不能修改），主界面右下角可设置主题背景附加效果", "Apply, add, edit or delete themes (preset themes cannot be modified), and you can set additional effects on the style in the lower right corner of the main frame"});
        LANG_MAP.put("customOnly", new String[]{"仅显示自定义主题", "Only show customized themes"});

        LANG_MAP.put("settingTitle", new String[]{"设置", "Settings"});
        LANG_MAP.put("generalTab", new String[]{"常规", "General"});
        LANG_MAP.put("appearance", new String[]{"外观", "Appearance"});
        LANG_MAP.put("downloadAndCache", new String[]{"下载与缓存", "Download & Cache"});
        LANG_MAP.put("playback", new String[]{"播放与历史", "Playback & History"});
        LANG_MAP.put("hotkey", new String[]{"快捷键", "Hotkey"});
        LANG_MAP.put("autoUpdate", new String[]{"启动时自动检查更新", "Check for updates on startup"});
        LANG_MAP.put("videoOnly", new String[]{"播放视频时隐藏主界面", "Hide main frame while playing video"});
        LANG_MAP.put("lang", new String[]{"界面语言（重启程序后生效）：", "UI language(Take effect after restart app): "});
        LANG_MAP.put("font", new String[]{"界面字体（重启程序后生效）：", "UI font(Take effect after restart app): "});
        LANG_MAP.put("closeOption", new String[]{"关闭主界面时：", "Closing option: "});
        LANG_MAP.put("windowSize", new String[]{"窗口大小：", "Window size: "});
        LANG_MAP.put("showTabText", new String[]{"显示侧边栏文字", "Show sidebar text"});
        LANG_MAP.put("lyricAlignment", new String[]{"歌词对齐方式：", "Lyric alignment: "});
        LANG_MAP.put("left", new String[]{"居左", "Left"});
        LANG_MAP.put("center", new String[]{"居中", "Center"});
        LANG_MAP.put("right", new String[]{"居右", "Right"});
        LANG_MAP.put("specMaxHeight", new String[]{"频谱最大高度(≤%s像素)：", "Max spectrum view height(≤%s pixels): "});
        LANG_MAP.put("gsFactor", new String[]{"高斯模糊半径（半径越大越模糊）：", "Gaussian blur radius(Bigger radius, blurrier): "});
        LANG_MAP.put("darkerFactor", new String[]{"暗角滤镜因子（因子越小越暗）：", "Darker mode factor(Smaller factor, darker): "});
        LANG_MAP.put("autoDownloadLyric", new String[]{"下载歌曲时自动下载歌词", "Download lyrics while downloading tracks"});
//        LANG_MAP.put("verbatimTimeline", new String[]{"歌词文件添加逐字时间轴", "Lyric files add verbatim timelines"});
        LANG_MAP.put("musicDown", new String[]{"歌曲下载路径：", "Track download path: "});
        LANG_MAP.put("mvDown", new String[]{"MV 下载路径：", "MV download path: "});
        LANG_MAP.put("cache", new String[]{"缓存路径：", "Cache path: "});
        LANG_MAP.put("maxCacheSize", new String[]{"最大缓存大小(≤%sMB)：", "Max cache size(≤%sMB): "});
        LANG_MAP.put("audioQuality", new String[]{"优先音质（如果可用）：", "Preferred audio quality(If available): "});
        LANG_MAP.put("videoQuality", new String[]{"优先画质（如果可用）：", "Preferred video quality(If available): "});
        LANG_MAP.put("fob", new String[]{"快进/快退时间：", "Fast forward/Rewind interval: "});
        LANG_MAP.put("balance", new String[]{"声道平衡：", "Audio channel: "});
        LANG_MAP.put("maxHistoryCount", new String[]{"最大播放历史数量(≤%s)：", "Max playback history count(≤%s): "});
        LANG_MAP.put("maxSearchHistoryCount", new String[]{"最大搜索历史数量(≤%s)：", "Max search history count(≤%s): "});
        LANG_MAP.put("maxConcurrentTaskCount", new String[]{"同时下载的最大任务数(≤%s)：", "Maximum number of active downloads(≤%s)："});
        LANG_MAP.put("backupAndRestore", new String[]{"播放列表备份/恢复（仅包括本地音乐列表、所有收藏列表）", "Playback list backup/restore(Local music and collection only)："});
        LANG_MAP.put("key", new String[]{"全局快捷键：", "Hotkeys: "});
        LANG_MAP.put("enableKey", new String[]{"是否启用", "Enable Hotkeys"});
        LANG_MAP.put("playOrPause", new String[]{"播放/暂停控制：", "Play/Pause: "});
        LANG_MAP.put("playLast", new String[]{"上一首：", "Previous: "});
        LANG_MAP.put("playNext", new String[]{"下一首：", "Next: "});
        LANG_MAP.put("backward", new String[]{"快退：", "Rewind: "});
        LANG_MAP.put("forward", new String[]{"快进：", "Fast forward: "});
        LANG_MAP.put("videoFullScreen", new String[]{"视频全屏切换：", "Video fullscreen: "});
        LANG_MAP.put("applySuccess", new String[]{"应用成功", "Applied successfully"});
        LANG_MAP.put("chooseFolder", new String[]{"选择文件夹", "Choose folder"});
        LANG_MAP.put("change", new String[]{"更改", "Change"});
        LANG_MAP.put("jsonFile", new String[]{"Json 文件", "Json files"});
        LANG_MAP.put("backup", new String[]{"备份", "Backup"});
        LANG_MAP.put("saveFile", new String[]{"保存文件", "Save file"});
        LANG_MAP.put("backupSuccess", new String[]{"备份成功", "Backup successfully"});
        LANG_MAP.put("backupFailed", new String[]{"备份失败", "Backup failed"});
        LANG_MAP.put("restore", new String[]{"恢复", "Restore"});
        LANG_MAP.put("chooseFile", new String[]{"选择文件", "Choose file"});
        LANG_MAP.put("restoreSuccess", new String[]{"恢复成功", "Restore successfully"});
        LANG_MAP.put("seconds", new String[]{" 秒", " s"});
        LANG_MAP.put("leftChannel", new String[]{"左声道", "Left"});
        LANG_MAP.put("stereo", new String[]{"立体声", "Stereo"});
        LANG_MAP.put("rightChannel", new String[]{"右声道", "Right"});
        LANG_MAP.put("invalidMusicDown", new String[]{"歌曲下载路径无效", "Invalid track download path"});
        LANG_MAP.put("invalidMvDown", new String[]{"MV 下载路径无效", "Invalid MV download path"});
        LANG_MAP.put("invalidCache", new String[]{"缓存路径无效", "Invalid cache path"});
        LANG_MAP.put("invalidSpecMaxHeight", new String[]{"频谱最大高度无效", "Invalid max spectrum height"});
        LANG_MAP.put("invalidMaxCacheSize", new String[]{"最大缓存大小无效", "Invalid max cache size"});
        LANG_MAP.put("invalidHistoryCount", new String[]{"最大播放历史数量无效", "Invalid playback history count"});
        LANG_MAP.put("invalidSearchHistoryCount", new String[]{"最大搜索历史数量无效", "Invalid search history count"});
        LANG_MAP.put("invalidConcurrentTaskCount", new String[]{"同时下载的最大任务数无效", "Invalid max concurrent task count"});

        LANG_MAP.put("soundEffectTitle", new String[]{"音效", "Sound effect"});
        LANG_MAP.put("soundEffect", new String[]{"音效：", "Sound effect: "});

        LANG_MAP.put("downloadMsg", new String[]{"正在下载更新包......", "Downloading update package..."});
        LANG_MAP.put("downloadFailedMsg", new String[]{"更新包下载失败，请稍后再试", "Update package download failed, please try again later"});
        LANG_MAP.put("validatingMsg", new String[]{"正在校验更新包完整性......", "Checking update package integrity..."});
        LANG_MAP.put("validationFailedMsg", new String[]{"更新包已损坏，请重新下载", "Update package is corrupted, please download again"});

        LANG_MAP.put("nc", new String[]{"小意", "NC"});
        LANG_MAP.put("kg", new String[]{"小枸", "KG"});
        LANG_MAP.put("qq", new String[]{"小丘", "QQ"});
        LANG_MAP.put("kw", new String[]{"小窝", "KW"});
        LANG_MAP.put("mg", new String[]{"小蜜", "MG"});
        LANG_MAP.put("xm", new String[]{"小希", "XM"});
        LANG_MAP.put("qi", new String[]{"小倩", "QI"});
        LANG_MAP.put("me", new String[]{"小猫", "ME"});
        LANG_MAP.put("hk", new String[]{"小看", "HK"});
        LANG_MAP.put("db", new String[]{"小豆", "DB"});
        LANG_MAP.put("dt", new String[]{"小糖", "DT"});
        LANG_MAP.put("bi", new String[]{"小哔", "BI"});
        LANG_MAP.put("hf", new String[]{"小磁", "HF"});
        LANG_MAP.put("gg", new String[]{"小咕", "GG"});
        LANG_MAP.put("fs", new String[]{"小五", "FS"});
        LANG_MAP.put("yy", new String[]{"小悦", "YY"});
        LANG_MAP.put("fa", new String[]{"发姐", "FA"});
        LANG_MAP.put("lz", new String[]{"李志", "LZ"});
        LANG_MAP.put("qs", new String[]{"小汽", "QS"});
        LANG_MAP.put("all", new String[]{"全部", "All"});

        LANG_MAP.put("off", new String[]{"关闭", "Off"});
        LANG_MAP.put("pop", new String[]{"流行", "Pop"});
        LANG_MAP.put("dance", new String[]{"舞曲", "Dance"});
        LANG_MAP.put("live", new String[]{"现场", "Live"});
        LANG_MAP.put("blue", new String[]{"蓝调", "Blue"});
        LANG_MAP.put("classic", new String[]{"古典", "Classic"});
        LANG_MAP.put("jazz", new String[]{"爵士", "Jazz"});
        LANG_MAP.put("slow", new String[]{"慢歌", "Slow"});
        LANG_MAP.put("electronic", new String[]{"电子", "Electronic"});
        LANG_MAP.put("rock", new String[]{"摇滚", "Rock"});
        LANG_MAP.put("country", new String[]{"乡村", "Country"});
        LANG_MAP.put("vocal", new String[]{"人声", "Vocal"});
        LANG_MAP.put("acg", new String[]{"ACG", "ACG"});
        LANG_MAP.put("acgFemale", new String[]{"ACG 女声", "ACG female voice"});
        LANG_MAP.put("chinese", new String[]{"国风", "Chinese"});
        LANG_MAP.put("folk", new String[]{"民谣", "Folk"});
        LANG_MAP.put("rap", new String[]{"说唱", "Rap"});
        LANG_MAP.put("soft", new String[]{"柔和", "Soft"});
        LANG_MAP.put("softBass", new String[]{"柔和低音", "Soft bass"});
        LANG_MAP.put("softTreble", new String[]{"柔和高音", "Soft treble"});
        LANG_MAP.put("treble", new String[]{"高音", "Treble"});
        LANG_MAP.put("middle", new String[]{"中音", "Middle"});
        LANG_MAP.put("bass", new String[]{"低音", "Bass"});
        LANG_MAP.put("bassTreble", new String[]{"低音 & 高音", "Bass & Treble"});
        LANG_MAP.put("bassBoost", new String[]{"重低音", "Bass boost"});
        LANG_MAP.put("bassExtreme", new String[]{"超重低音", "Bass extreme"});
        LANG_MAP.put("speaker", new String[]{"扬声器（响亮）", "Speaker(Loud)"});
        LANG_MAP.put("trumpet", new String[]{"村口大喇叭", "Trumpet"});
        LANG_MAP.put("highQuality", new String[]{"高解析", "High quality"});
        LANG_MAP.put("pitchDecay", new String[]{"高音衰减", "Pitch decay"});
        LANG_MAP.put("scaryHorn", new String[]{"喇叭炸机专用", "Scary horn"});
        LANG_MAP.put("oldPhone", new String[]{"老年机/儿童玩具", "Old phone/Toy"});

        LANG_MAP.put("master", new String[]{"至臻母带 (FLAC)", "Master (FLAC)"});
        LANG_MAP.put("atmosphere", new String[]{"至臻全景声 (FLAC)", "Atmosphere (FLAC)"});
        LANG_MAP.put("hires", new String[]{"Hi-Res (FLAC)", "Hi-Res (FLAC)"});
        LANG_MAP.put("lossless", new String[]{"无损 (FLAC)", "Lossless (FLAC)"});
        LANG_MAP.put("super", new String[]{"超高 (MP3)", "Super (MP3)"});
        LANG_MAP.put("high", new String[]{"高 (MP3)", "High (MP3)"});
        LANG_MAP.put("standard", new String[]{"普通 (MP3)", "Standard (MP3)"});

        LANG_MAP.put("hr", new String[]{"HR", "HR"});
        LANG_MAP.put("sq", new String[]{"SQ", "SQ"});
        LANG_MAP.put("hq", new String[]{"HQ", "HQ"});
        LANG_MAP.put("mq", new String[]{"MQ", "MQ"});
        LANG_MAP.put("lq", new String[]{"LQ", "LQ"});

        LANG_MAP.put("uhd", new String[]{"超清 (2K)", "UHD (2K)"});
        LANG_MAP.put("fhd", new String[]{"全高清 (1080P)", "FHD (1080P)"});
        LANG_MAP.put("hd", new String[]{"高清 (720P)", "HD (720P)"});
        LANG_MAP.put("sd", new String[]{"标清 (480P)", "SD (480P)"});
        LANG_MAP.put("fluent", new String[]{"流畅 (360P)", "Fluent (360P)"});

        LANG_MAP.put("downloading", new String[]{"下载中", "Downloading"});
        LANG_MAP.put("completed", new String[]{"已完成", "Completed"});
        LANG_MAP.put("interrupted", new String[]{"已中断", "Interrupted"});
        LANG_MAP.put("failed", new String[]{"失败", "Failed"});
        LANG_MAP.put("waiting", new String[]{"等待中", "Waiting"});

        LANG_MAP.put("track", new String[]{"歌曲", "Track"});
        LANG_MAP.put("mv", new String[]{"MV", "MV"});

        LANG_MAP.put("smaller", new String[]{"较小", "Smaller"});
        LANG_MAP.put("small", new String[]{"小", "Small"});
        LANG_MAP.put("medium", new String[]{"中", "Medium"});
        LANG_MAP.put("large", new String[]{"大", "Large"});
        LANG_MAP.put("larger", new String[]{"较大", "Larger"});
        LANG_MAP.put("sLarge", new String[]{"超大", "Sup large"});
        LANG_MAP.put("huge", new String[]{"极大", "Huge"});

        LANG_MAP.put("default", new String[]{"默认", "Default"});
        LANG_MAP.put("day", new String[]{"白天", "Day"});
        LANG_MAP.put("night", new String[]{"夜晚", "Night"});
        LANG_MAP.put("spring", new String[]{"春", "Spring"});
        LANG_MAP.put("summer", new String[]{"夏", "Summer"});
        LANG_MAP.put("autumn", new String[]{"秋", "Autumn"});
        LANG_MAP.put("winter", new String[]{"冬", "Winter"});
        LANG_MAP.put("pinkParadise", new String[]{"粉红天堂", "Pink paradise"});
        LANG_MAP.put("minimalism", new String[]{"极简主义", "Minimalism"});
        LANG_MAP.put("abstractLandscape", new String[]{"抽象风景", "Abstract landscape"});
        LANG_MAP.put("sunsetOcean", new String[]{"落日海洋", "Sunset ocean"});
        LANG_MAP.put("starrySunset", new String[]{"落日星空", "Starry sunset"});
        LANG_MAP.put("coniferousForest", new String[]{"针叶林", "Coniferous forest"});
        LANG_MAP.put("she", new String[]{"她", "She"});
        LANG_MAP.put("luming", new String[]{"鹿鸣", "Lu ming"});
        LANG_MAP.put("yourName", new String[]{"你的名字", "Your name"});
        LANG_MAP.put("deepSea", new String[]{"深海", "Deep sea"});
        LANG_MAP.put("brook", new String[]{"林间小溪", "Brook"});
        LANG_MAP.put("happyNewYear", new String[]{"新年快乐", "Happy new year"});

        LANG_MAP.put("flat", new String[]{"平地式", "Flat"});
        LANG_MAP.put("lifted", new String[]{"悬空式", "Lifted"});
        LANG_MAP.put("polyline", new String[]{"折线式", "Polyline"});
        LANG_MAP.put("curve", new String[]{"曲线式", "Curve"});
        LANG_MAP.put("summit", new String[]{"山峰式", "Summit"});
        LANG_MAP.put("wave", new String[]{"波浪式", "Wave"});
        LANG_MAP.put("symSummit", new String[]{"对称山峰式", "Sym summit"});
        LANG_MAP.put("symWave", new String[]{"对称波浪式", "Sym wave"});

        LANG_MAP.put("closingAsk", new String[]{"询问", "Ask"});
        LANG_MAP.put("closingHide", new String[]{"隐藏到托盘", "Hide to tray"});
        LANG_MAP.put("closingExit", new String[]{"退出程序", "Exit"});

        LANG_MAP.put("volume", new String[]{"音量：", "Volume: "});
    }

    /**
     * 获取语言文本
     *
     * @param key
     * @return
     */
    public static String getText(String key) {
        return LANG_MAP.get(key)[solidLang];
    }
}
